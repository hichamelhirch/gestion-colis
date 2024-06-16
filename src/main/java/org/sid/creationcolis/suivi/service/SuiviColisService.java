package org.sid.creationcolis.suivi.service;

import jakarta.transaction.Transactional;
import org.sid.creationcolis.dtos.ColisDTO;
import org.sid.creationcolis.dtos.HubDTO;
import org.sid.creationcolis.entities.Colis;


import org.sid.creationcolis.entities.Hub;
import org.sid.creationcolis.entities.User;
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
import org.sid.creationcolis.suivi.notifications.EmailNotificationService;
import org.sid.creationcolis.suivi.notifications.WhatsAppNotificationService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

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
    private final InfobipWhatsAppService infobipWhatsAppService;
   private final WhatsAppNotificationService whatsAppNotificationService;
   private final EmailNotificationService emailNotificationService;

    public SuiviColisService(SuiviColisRepository suiviColisRepository,
                             SuiviColisHistoriqueRepository suiviColisHistoriqueRepository,
                             ColisService colisService,
                             HubService hubService,
                             LivreurRepository livreurRepository, ColisMapper colisMapper, HubMapper hubMapper, UserService userService, InfobipWhatsAppService infobipWhatsAppService, WhatsAppNotificationService whatsAppNotificationService, EmailNotificationService emailNotificationService) {
        this.suiviColisRepository = suiviColisRepository;
        this.suiviColisHistoriqueRepository = suiviColisHistoriqueRepository;
        this.colisService = colisService;
        this.hubService = hubService;
        this.livreurRepository = livreurRepository;
        this.colisMapper = colisMapper;
        this.hubMapper = hubMapper;
        this.userService = userService;
        this.infobipWhatsAppService = infobipWhatsAppService;
        this.whatsAppNotificationService = whatsAppNotificationService;
        this.emailNotificationService = emailNotificationService;
    }

    public List<SuiviColis> getSuiviByColisId(Long colisId) {
        return suiviColisRepository.findByColisId(colisId);
    }

    public SuiviColis ramasserColis(Long colisId, Long livreurId) {
        ColisDTO colisDTO = colisService.getColisById(colisId);
        Colis colis = colisMapper.toEntity(colisDTO);

        Livreur livreur = livreurRepository.findById(livreurId).orElseThrow(() -> new LivreurNotFoundException("Livreur not found with id " + livreurId));

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

    public SuiviColis traiterColis(Long colisId, Long hubId) {
        ColisDTO colisDTO = colisService.getColisById(colisId);
        Colis colis = colisMapper.toEntity(colisDTO);
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

  /*  public SuiviColis livrerColis(Long colisId, Long livreurId) {
        ColisDTO colisDTO = colisService.getColisById(colisId);
        Colis colis = colisMapper.toEntity(colisDTO);
        Livreur livreur = livreurRepository.findById(livreurId).orElseThrow(() -> new LivreurNotFoundException("Livreur not found with id " + livreurId));

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


   */

    @Transactional
    public SuiviColis livrerColisWhatSap(Long colisId, Long livreurId) throws IOException {
        // Recuperer le DTO du colis
        ColisDTO colisDTO = colisService.getColisById(colisId);

        // Convertir le DTO en entite Colis
        Colis colis = colisMapper.toEntity(colisDTO);

        // Récupérer le livreur
        Livreur livreur = livreurRepository.findById(livreurId)
                .orElseThrow(() -> new RuntimeException("Livreur not found with id " + livreurId));

        // Envoyer une notification WhatsApp au chargeur
       String chargeurTel = colis.getNumTelChargeur();
        String message = "Votre colis " + colis.getNumeroColis() + " a été livré.";
        whatsAppNotificationService.sendWhatsAppMessage(chargeurTel, message);

        // Archiver les anciens SuiviColis
        archiveOldSuiviColis(colis);

        // Creer un nouveau suivi de colis
        SuiviColis suiviColis = SuiviColis.builder()
                .colis(colis)
                .statut(StatutSuiviColis.LIVRE)
                .dateSuivi(LocalDateTime.now())
                .description("Colis livré par " + livreur.getNomComplet())
                .livreur(livreur)
                .build();

        // Sauvegarder le nouveau suivi de colis
        return suiviColisRepository.save(suiviColis);
    }




    @Transactional
    public SuiviColis livrerColis(Long colisId, Long livreurId) {
        ColisDTO colisDTO = colisService.getColisById(colisId);
        Colis colis = colisMapper.toEntity(colisDTO);
        Livreur livreur = livreurRepository.findById(livreurId)
                .orElseThrow(() -> new RuntimeException("Livreur not found with id " + livreurId));

        // Recuperer l'email du client associe au colis
        User user = userService.getUserByClientId(colis.getClient().getId());
        if (user == null) {
            throw new RuntimeException("User not found for client with id " + colis.getClient().getId());
        }
        String chargeurEmail = user.getEmail();
        String subject = "Votre colis a été livré";
        String message = "Votre colis " + colis.getNumeroColis() + " a été livré.";
        emailNotificationService.sendEmail(chargeurEmail, subject, message);

        // Archiver les anciens SuiviColis
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

}
