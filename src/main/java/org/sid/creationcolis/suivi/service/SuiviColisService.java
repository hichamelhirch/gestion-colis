package org.sid.creationcolis.suivi.service;

import jakarta.transaction.Transactional;
import org.sid.creationcolis.dtos.ColisDTO;
import org.sid.creationcolis.dtos.HubDTO;
import org.sid.creationcolis.entities.Colis;
import org.sid.creationcolis.entities.Hub;
import org.sid.creationcolis.entities.User;
import org.sid.creationcolis.enums.StatutColis;
import org.sid.creationcolis.mappers.ColisMapper;
import org.sid.creationcolis.mappers.HubMapper;
import org.sid.creationcolis.service.ColisService;
import org.sid.creationcolis.service.HubService;
import org.sid.creationcolis.service.UserService;
import org.sid.creationcolis.suivi.Entity.Livreur;
import org.sid.creationcolis.suivi.Entity.SuiviColis;
import org.sid.creationcolis.suivi.Entity.SuiviColisHistorique;
import org.sid.creationcolis.suivi.Repository.LivreurRepository;
import org.sid.creationcolis.suivi.Repository.SuiviColisHistoriqueRepository;
import org.sid.creationcolis.suivi.Repository.SuiviColisRepository;
import org.sid.creationcolis.suivi.enums.StatutSuiviColis;
import org.sid.creationcolis.suivi.exceptions.LivreurNotFoundException;
import org.sid.creationcolis.suivi.exceptions.SuiviColisNotFoundException;
import org.sid.creationcolis.suivi.notifications.EmailNotificationService;
import org.sid.creationcolis.suivi.notifications.WhatsAppNotificationService;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SuiviColisService {

    private final SuiviColisRepository suiviColisRepository;
    private final SuiviColisHistoriqueRepository suiviColisHistoriqueRepository;
    private final ColisService colisService;
    private final HubService hubService;
    private final LivreurRepository livreurRepository;
    private final ColisMapper colisMapper;
    private final HubMapper hubMapper;
    private final UserService userService;
    private final WhatsAppNotificationService whatsAppNotificationService;
    private final EmailNotificationService emailNotificationService;

    public SuiviColisService(SuiviColisRepository suiviColisRepository,
                             SuiviColisHistoriqueRepository suiviColisHistoriqueRepository,
                             ColisService colisService,
                             HubService hubService,
                             LivreurRepository livreurRepository, ColisMapper colisMapper, HubMapper hubMapper, UserService userService, WhatsAppNotificationService whatsAppNotificationService, EmailNotificationService emailNotificationService) {
        this.suiviColisRepository = suiviColisRepository;
        this.suiviColisHistoriqueRepository = suiviColisHistoriqueRepository;
        this.colisService = colisService;
        this.hubService = hubService;
        this.livreurRepository = livreurRepository;
        this.colisMapper = colisMapper;
        this.hubMapper = hubMapper;
        this.userService = userService;
        this.whatsAppNotificationService = whatsAppNotificationService;
        this.emailNotificationService = emailNotificationService;
    }
    @Transactional
    public List<SuiviColis> getSuiviByColisId(Long colisId) {
        return suiviColisRepository.findByColisId(colisId);
    }
   // @CachePut(value = "colisCache", key = "#colisId")
    @Transactional
    public SuiviColis ramasserColis(Long colisId, Long livreurId) {
        ColisDTO colisDTO = colisService.getColisById(colisId);
        Colis colis = colisMapper.toEntity(colisDTO);

        if (colis.getStatusColis() != StatutColis.CONFIRMER) {
            throw new RuntimeException("Le colis doit être confirmé avant de pouvoir être ramassé.");
        }

        Optional<SuiviColis> dernierSuiviOpt = suiviColisRepository.findTopByColisOrderByDateSuiviDesc(colis);
        SuiviColis dernierSuivi = dernierSuiviOpt.orElse(null);

        if (dernierSuivi != null && (dernierSuivi.getStatut() == StatutSuiviColis.LIVRE || dernierSuivi.getStatut() == StatutSuiviColis.A_RAMASSER)) {
            throw new RuntimeException("Le colis a déjà été ramassé ou livré et ne peut être ramassé à nouveau.");
        }

        Livreur livreur = livreurRepository.findById(livreurId)
                .orElseThrow(() -> new LivreurNotFoundException("Livreur not found with id " + livreurId));

        archiveOldSuiviColis(colis);

        SuiviColis suiviColis = SuiviColis.builder()
                .colis(colis)
                .statut(StatutSuiviColis.A_RAMASSER)
                .dateSuivi(LocalDateTime.now())
                .description("Colis ramassé par " + livreur.getNomComplet())
                .livreur(livreur)
                .build();
        return suiviColisRepository.save(suiviColis);
    }




    // @CachePut(value = "colisCache", key = "#colisId")
    @Transactional
    public SuiviColis traiterColis(Long colisId, Long hubId) {
        ColisDTO colisDTO = colisService.getColisById(colisId);
        Colis colis = colisMapper.toEntity(colisDTO);

        if (colis.getStatusColis() != StatutColis.CONFIRMER) {
            throw new RuntimeException("Le colis doit être confirmé avant de pouvoir être traité.");
        }

        Optional<SuiviColis> dernierSuiviOpt = suiviColisRepository.findTopByColisOrderByDateSuiviDesc(colis);
        SuiviColis dernierSuivi = dernierSuiviOpt.orElseThrow(() -> new RuntimeException("Aucun suivi trouvé pour ce colis."));

        if (dernierSuivi.getStatut() != StatutSuiviColis.A_RAMASSER) {
            throw new RuntimeException("Le colis doit être ramassé avant de pouvoir être traité.");
        }

        HubDTO hubDTO = hubService.getHubById(hubId);
        Hub hub = hubMapper.toEntity(hubDTO);

        archiveOldSuiviColis(colis);

        SuiviColis suiviColis = SuiviColis.builder()
                .colis(colis)
                .statut(StatutSuiviColis.EN_COURS_DE_TRAITEMENT)
                .dateSuivi(LocalDateTime.now())
                .description("Colis en cours de traitement dans le hub " + hub.getNameHub())
                .hub(hub)
                .build();
        return suiviColisRepository.save(suiviColis);
    }


    // @CachePut(value = "colisCache", key = "#colisId")
    @Transactional
    public SuiviColis mettreEnCoursDeLivraison(Long colisId, Long livreurId) {
        ColisDTO colisDTO = colisService.getColisById(colisId);
        Colis colis = colisMapper.toEntity(colisDTO);

        if (colis.getStatusColis() != StatutColis.CONFIRMER) {
            throw new RuntimeException("Le colis doit être confirmé avant de pouvoir être mis en cours de livraison.");
        }

        Optional<SuiviColis> dernierSuiviOpt = suiviColisRepository.findTopByColisOrderByDateSuiviDesc(colis);
        SuiviColis dernierSuivi = dernierSuiviOpt.orElseThrow(() -> new RuntimeException("Aucun suivi trouvé pour ce colis."));

        if (dernierSuivi.getStatut() != StatutSuiviColis.A_RAMASSER && dernierSuivi.getStatut() != StatutSuiviColis.EN_COURS_DE_TRAITEMENT) {
            throw new RuntimeException("Le colis doit être ramassé ou en cours de traitement avant de pouvoir être mis en cours de livraison.");
        }

        Livreur livreur = livreurRepository.findById(livreurId)
                .orElseThrow(() -> new RuntimeException("Livreur not found with id " + livreurId));

        archiveOldSuiviColis(colis);

        SuiviColis suiviColis = SuiviColis.builder()
                .colis(colis)
                .statut(StatutSuiviColis.EN_COURS_DE_LIVRAISON)
                .dateSuivi(LocalDateTime.now())
                .description("Colis en cours de livraison par " + livreur.getNomComplet())
                .livreur(livreur)
                .build();
        return suiviColisRepository.save(suiviColis);
    }


    // @CachePut(value = "colisCache", key = "#colisId")
    @Transactional
    public SuiviColis livrerColis(Long colisId, Long livreurId) {
        ColisDTO colisDTO = colisService.getColisById(colisId);
        Colis colis = colisMapper.toEntity(colisDTO);

        if (colis.getStatusColis() != StatutColis.CONFIRMER) {
            throw new RuntimeException("Le colis avec ID " + colisId+"doit être confirmé avant de pouvoir être livré.");
        }

        Optional<SuiviColis> dernierSuiviOpt = suiviColisRepository.findTopByColisOrderByDateSuiviDesc(colis);
        SuiviColis dernierSuivi = dernierSuiviOpt.orElseThrow(() -> new RuntimeException("Aucun suivi trouvé pour ce colis."));

        if (dernierSuivi.getStatut() != StatutSuiviColis.A_RAMASSER &&
                dernierSuivi.getStatut() != StatutSuiviColis.EN_COURS_DE_TRAITEMENT &&
                dernierSuivi.getStatut() != StatutSuiviColis.EN_COURS_DE_LIVRAISON) {
            throw new RuntimeException("Le colis doit être ramassé, en cours de traitement, ou en cours de livraison avant de pouvoir être livré.");
        }

        Livreur livreur = livreurRepository.findById(livreurId)
                .orElseThrow(() -> new RuntimeException("Livreur not found with id " + livreurId));

        User user = userService.getUserByClientId(colis.getClient().getId());
        if (user == null) {
            throw new RuntimeException("User not found for client with id " + colis.getClient().getId());
        }
        String chargeurEmail = user.getEmail();
        String subject = "Votre colis a été livré";
        String message = "Votre colis " + colis.getNumeroColis() + " a été livré.";
        emailNotificationService.sendEmail(chargeurEmail, subject, message);

        archiveOldSuiviColis(colis);

        SuiviColis suiviColis = SuiviColis.builder()
                .colis(colis)
                .statut(StatutSuiviColis.LIVRE)
                .dateSuivi(LocalDateTime.now())
                .description("Colis livré par " + livreur.getNomComplet())
                .livreur(livreur)
                .build();

        return suiviColisRepository.save(suiviColis);
    }



    private void archiveOldSuiviColis(Colis colis) {
        List<SuiviColis> oldSuiviColis = suiviColisRepository.findByColis(colis);
        for (SuiviColis sc : oldSuiviColis) {
            SuiviColisHistorique historique = new SuiviColisHistorique();
            historique.setColis(sc.getColis());
            historique.setStatut(sc.getStatut());
            historique.setDateSuivi(sc.getDateSuivi());
            historique.setDescription(sc.getDescription());
            suiviColisHistoriqueRepository.save(historique);
            suiviColisRepository.delete(sc);
        }
    }
    public SuiviColis updateDescriptionProbleme(Long colisId, String description, String type, Long id) {
        ColisDTO colisDTO = colisService.getColisById(colisId);
        Colis colis = colisMapper.toEntity(colisDTO);

        List<SuiviColis> suiviColisList = suiviColisRepository.findByColis(colis);
        if (suiviColisList.isEmpty()) {
            throw new SuiviColisNotFoundException("SuiviColis not found for colis with id " + colisId);
        }

        // On suppose qu'il n'y a qu'un seul SuiviColis par Colis, prenez le premier élément
        SuiviColis suiviColis = suiviColisList.get(0);

        // Supprimer la vérification du statut
        /*
        if (suiviColis.getStatut() == StatutSuiviColis.A_RAMASSER) {
            throw new IllegalStateException("La description du problème ne peut être mise à jour qu'après le ramassage.");
        }
        */

        if ("livreur".equals(type)) {
            livreurRepository.findById(id)
                    .orElseThrow(() -> new LivreurNotFoundException("Livreur not found with id " + id));
        } else if ("hub".equals(type)) {
            hubService.getHubById(id);
        } else {
            throw new IllegalArgumentException("Type inconnu: " + type);
        }

        suiviColis.setDescriptionProbleme(description);
        return suiviColisRepository.save(suiviColis);
    }

    public void updateSuiviColisStatut(Long colisId, StatutSuiviColis statut) throws IOException {
        List<SuiviColis> suiviColisList = suiviColisRepository.findByColisId(colisId);
        if (suiviColisList.isEmpty()) {
            throw new RuntimeException("Colis not found with id " + colisId);
        }

        SuiviColis suiviColis = suiviColisList.get(0);

        suiviColis.setStatut(statut);
        suiviColisRepository.save(suiviColis);

        if (statut == StatutSuiviColis.LIVRE) {
            String message = "Votre colis avec ID " + colisId + " a été livré.";
            whatsAppNotificationService.sendWhatsAppMessage(suiviColis.getColis().getNumTelChargeur(), message);
        }
    }

  /*  public List<String> getOperationsByBarcode(String barcode) {
        ColisDTO colisDTO = colisService.getColisByBarcode(barcode);
        if (colisDTO == null) {
            throw new RuntimeException("Colis not found with barcode " + barcode);
        }
        Colis colis = colisMapper.toEntity(colisDTO);
        List<SuiviColis> suiviColisList = suiviColisRepository.findByColis(colis);
        List<String> operations = new ArrayList<>();
        for (SuiviColis suiviColis : suiviColisList) {
            String operation = "Date: " + suiviColis.getDateSuivi() +
                    ", Status: " + suiviColis.getStatut() +
                    ", Description: " + suiviColis.getDescription();
            operations.add(operation);
        }
        return operations;
    }

   */

    public List<String> getOperationsByBarcode(String barcode) {
        ColisDTO colisDTO = colisService.getColisByBarcode(barcode);
        if (colisDTO == null) {
            throw new RuntimeException("Colis not found with barcode " + barcode);
        }
        Colis colis = colisMapper.toEntity(colisDTO);
        List<SuiviColis> suiviColisList = suiviColisRepository.findByColis(colis);
        List<SuiviColisHistorique> suiviColisHistoriqueList = suiviColisHistoriqueRepository.findByColis(colis);

        List<String> operations = new ArrayList<>();
        for (SuiviColis suiviColis : suiviColisList) {
            String operation = "Date: " + suiviColis.getDateSuivi() +
                    ", Status: " + suiviColis.getStatut() +
                    ", Description: " + suiviColis.getDescription();
            operations.add(operation);
        }

        for (SuiviColisHistorique suiviColisHistorique : suiviColisHistoriqueList) {
            String operation = "Date: " + suiviColisHistorique.getDateSuivi() +
                    ", Status: " + suiviColisHistorique.getStatut() +
                    ", Description: " + suiviColisHistorique.getDescription();
            operations.add(operation);
        }

        // Trier les opérations par date decroissante
        operations.sort((op1, op2) -> {
            String date1 = op1.split(",")[0].split(": ")[1];
            String date2 = op2.split(",")[0].split(": ")[1];
            return date2.compareTo(date1);
        });

        return operations;
    }
    public List<ColisDTO> getAllColisLivres() {
        List<SuiviColis> suiviColisLivres = suiviColisRepository.findByStatut(StatutSuiviColis.LIVRE);
        return suiviColisLivres.stream()
                .map(SuiviColis::getColis)
                .distinct()
                .map(colisMapper::toDTO)
                .collect(Collectors.toList());
    }

}
