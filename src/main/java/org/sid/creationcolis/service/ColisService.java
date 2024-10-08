package org.sid.creationcolis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.sid.creationcolis.config.BarcodeGenerator;
import org.sid.creationcolis.dtos.ColisDTO;
import org.sid.creationcolis.dtos.ServiceALivraisonDTO;
import org.sid.creationcolis.entities.*;
import org.sid.creationcolis.enums.StatutColis;
import org.sid.creationcolis.enums.TypeLivraison;
import org.sid.creationcolis.mappers.ColisMapper;
import org.sid.creationcolis.mappers.ServiceALivraisonMapper;
import org.sid.creationcolis.mappers.VilleMapper;
import org.sid.creationcolis.payment.TokenService;
import org.sid.creationcolis.paymentStripe.StripeService;
import org.sid.creationcolis.repository.ColisRepository;
import org.sid.creationcolis.repository.ServiceALivraisonRepository;
import org.sid.creationcolis.repository.UserRepository;
import org.sid.creationcolis.repository.VilleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ColisService {
    private static final Logger logger = LoggerFactory.getLogger(ColisService.class);
    private static final ThreadLocal<ObjectMapper> objectMapperThreadLocal = ThreadLocal.withInitial(ObjectMapper::new);

    private final RestTemplate restTemplate;

    private final ServiceALivraisonRepository serviceALivraisonRepository;
    private final ColisRepository colisRepository;
    private final ColisMapper colisMapper;
    private final UserRepository userRepository;
    private final VilleRepository villeRepository;
    private final ClientService clientService;
    private final ThreadPoolTaskScheduler threadPoolTaskScheduler;
    private final BarcodeGenerator barcodeGenerator;

    private final VilleMapper villeMapper;
    private final ServiceALivraisonRepository serviceRepository;
    private final ServiceALivraisonMapper serviceALivraisonMapper;

    private final VilleService villeService;
    private final CamundaService camundaService;
    private final StripeService stripeService;
    private final TokenService tokenService;
    private final NotificationService notificationService;

    @Autowired
    public ColisService(RestTemplate restTemplate, ServiceALivraisonRepository serviceALivraisonRepository, ColisRepository colisRepository, ColisMapper colisMapper, UserRepository userRepository, VilleRepository villeRepository, ClientService clientService, ThreadPoolTaskScheduler threadPoolTaskScheduler, BarcodeGenerator barcodeGenerator, VilleMapper villeMapper, ServiceALivraisonRepository serviceRepository, ServiceALivraisonMapper serviceALivraisonMapper, VilleService villeService, CamundaService camundaService, StripeService stripeService, TokenService tokenService, NotificationService notificationService) {
        this.restTemplate = restTemplate;
        this.serviceALivraisonRepository = serviceALivraisonRepository;
        this.colisRepository = colisRepository;
        this.colisMapper = colisMapper;
        this.userRepository = userRepository;
        this.villeRepository = villeRepository;
        this.clientService = clientService;
        this.threadPoolTaskScheduler = threadPoolTaskScheduler;
        this.barcodeGenerator = barcodeGenerator;
        this.villeMapper = villeMapper;
        this.serviceRepository = serviceRepository;
        this.serviceALivraisonMapper = serviceALivraisonMapper;
        this.villeService = villeService;
        this.camundaService = camundaService;
        this.stripeService = stripeService;
        this.tokenService = tokenService;
        this.notificationService = notificationService;
    }





    public List<ColisDTO> getAllColis() {
        logger.debug("Fetching all colis");
        List<ColisDTO> colis = colisRepository.findAll().stream()
                .map(colisMapper::toDTO)
                .collect(Collectors.toList());
        logger.info("Fetched {} colis", colis.size());
        return colis;
    }

    public ColisDTO getColisById(Long id) {
        logger.debug("Fetching colis by ID: {}", id);
        Optional<Colis> colisOptional = colisRepository.findById(id);
        if (colisOptional.isPresent()) {
            logger.info("Fetched colis: {}", colisOptional.get());
            return colisMapper.toDTO(colisOptional.get());
        } else {
            logger.warn("Colis not found for ID: {}", id);
            return null;
        }
    }









    @Transactional
    public ColisDTO updateColis(Long id, ColisDTO colisDTO) {
        logger.debug("Updating colis with ID: {}", id);
        logger.debug("Received colisDTO: {}", colisDTO);

        Optional<Colis> colisOptional = colisRepository.findById(id);

        if (colisOptional.isPresent()) {
            Colis existingColis = colisOptional.get();
            BeanUtils.copyProperties(colisDTO, existingColis, "id", "frais", "numeroColis", "codeBarre");

            Ville villeDestinataire = villeRepository.findById(colisDTO.getVilleDestinataire().getId())
                    .orElseThrow(() -> new RuntimeException("Ville de destination non trouvée"));
            Ville villeDepart = villeRepository.findById(colisDTO.getVilleDepart().getId())
                    .orElseThrow(() -> new RuntimeException("Ville de départ non trouvée"));

            existingColis.setVilleDestinataire(villeDestinataire);
            existingColis.setVilleDepart(villeDepart);

            // Ajout des nouveaux services
            List<ServiceALivraison> existingServices = existingColis.getServices();
                 existingServices.clear();
            if (colisDTO.getServices() != null) {
                for (ServiceALivraisonDTO serviceDTO : colisDTO.getServices()) {
                    logger.debug("Processing serviceDTO: {}", serviceDTO);
                    ServiceALivraison service = serviceRepository.findById(serviceDTO.getId())
                            .orElseThrow(() -> new RuntimeException("Service non trouvé"));
                    if (!existingServices.contains(service)) {
                        existingServices.add(service);
                    }
                }
            }

            logger.debug("Updated services: {}", existingServices);

            Integer frais = camundaService.getFraisFromCamunda(existingColis.getTypeLivraison(), existingColis.getVilleDepart().getId(), existingColis.getVilleDestinataire().getId());
            if (frais != null) {
                existingColis.setFrais(frais);
            }

            existingColis.setStatusColis(StatutColis.BROUILLON);
            existingColis.setCreationDate(LocalDateTime.now());

            Colis updatedColis = colisRepository.save(existingColis);

            logger.debug("Colis updated: {}", updatedColis);

            return colisMapper.toDTO(updatedColis);
        } else {
            throw new RuntimeException("Colis non trouvé pour l'ID : " + id);
        }
    }

    @Transactional
    public ColisDTO addServiceToColis(Long id, Long newServiceId) {
        logger.debug("Adding service to colis with ID: {}", id);
        logger.debug("New service ID: {}", newServiceId);

        // Verifier si le service existe
        ServiceALivraison existingService = serviceALivraisonRepository.findById(newServiceId)
                .orElseThrow(() -> new RuntimeException("Service non trouvé"));

        // Recuperer le colis
        Optional<Colis> colisOptional = colisRepository.findById(id);

        if (colisOptional.isPresent()) {
            Colis existingColis = colisOptional.get();

            // Verifier si le service n'est pas deje associe au colis
            if (!existingColis.getServices().contains(existingService)) {
                existingColis.getServices().add(existingService);

                Colis updatedColis = colisRepository.save(existingColis);

                logger.debug("Service added to colis: {}", existingService);
                logger.debug("Updated colis: {}", updatedColis);

                return colisMapper.toDTO(updatedColis);
            } else {
                throw new RuntimeException("Le service est déjà associé au colis.");
            }
        } else {
            throw new RuntimeException("Colis non trouvé pour l'ID : " + id);
        }
    }


    public void removeServices(Long colisId, Long serviceId) {
        Colis colis = colisRepository.findById(colisId)
                .orElseThrow(() -> new RuntimeException("Colis non trouvé pour l'ID : " + colisId));

        ServiceALivraison serviceToRemove = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service non trouvé pour l'ID : " + serviceId));

        colis.getServices().remove(serviceToRemove);
        colisRepository.save(colis);
    }




    public void deleteColis(Long id) {
        logger.debug("Deleting colis by ID: {}", id);
        colisRepository.deleteById(id);
        logger.info("Deleted colis with ID: {}", id);
    }

   public Colis createColisFromClient(Client client, String nomDestinataire, String prenomDestinataire,
                                       String telDestinataire, String adresseDestinataire, Ville villeDestinataire) {
        Colis colis = new Colis();
        BeanUtils.copyProperties(client, colis);
        colis.setNomDestinataire(nomDestinataire);
        colis.setTelDestinataire(telDestinataire);
        colis.setAdresseDestinataire(adresseDestinataire);
        colis.setVilleDestinataire(villeDestinataire);
        return colis;
    }


    public Map<String, Object> prepareDataForCamunda(Long colisId) {
        try {
            Colis colis = colisRepository.findById(colisId).orElse(null);
            if (colis != null) {
                Ville villeDepart = colis.getVilleDepart();
                Ville villeDestinataire = colis.getVilleDestinataire();
                String typeLivraison = colis.getTypeLivraison().toString();
                String zoneVilleDepart = villeService.getZoneByVille(villeDepart.getId());
                String zoneVilleDestinataire = villeService.getZoneByVille(villeDestinataire.getId());
                String resultatDeuxVilles = villeService.getResultatDeuxVille(villeDepart.getId(), villeDestinataire.getId());
                String natureHub = villeService.villeNaturehub(villeDepart.getId(), villeDestinataire.getId());
                String villeDepartNature = villeService.getNatureVille(villeDepart.getId());
                String villeDestinataireNature = villeService.getNatureVille(villeDestinataire.getId());
                ObjectMapper objectMapper = objectMapperThreadLocal.get();
                Map<String, Object> variables = new HashMap<>();
                variables.put("TypeLivraison", Map.of("value", typeLivraison, "type", "String"));
                variables.put("RegionD", Map.of("value", zoneVilleDepart, "type", "String"));
                variables.put("RegionA", Map.of("value", zoneVilleDestinataire, "type", "String"));
                variables.put("Ville", Map.of("value", resultatDeuxVilles, "type", "String"));
                variables.put("Hub", Map.of("value", natureHub, "type", "String"));
                variables.put("VilleDepart", Map.of("value", villeDepartNature, "type", "String"));
                variables.put("VilleLiv", Map.of("value", villeDestinataireNature, "type", "String"));
                Map<String, Object> requestData = new HashMap<>();
                requestData.put("variables", variables);
                return requestData;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Integer getFraisFromCamunda(Long colisId) {
        try {
            Map<String, Object> requestData = prepareDataForCamunda(colisId);
            ObjectMapper objectMapper = objectMapperThreadLocal.get();
            String requestDataJson = objectMapper.writeValueAsString(requestData);
            String camundaDecisionUrl = "http://localhost:8080/engine-rest/decision-definition/key/Decision_1gnjh82/evaluate";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> requestEntity = new HttpEntity<>(requestDataJson, headers);
            ResponseEntity<Map[]> responseEntity = restTemplate.exchange(
                    camundaDecisionUrl,
                    HttpMethod.POST,
                    requestEntity,
                    Map[].class
            );
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> response = responseEntity.getBody()[0];
                if (response != null && response.containsKey("Frais")) {
                    Map<String, Object> fraisMap = (Map<String, Object>) response.get("Frais");
                    if (fraisMap != null && fraisMap.containsKey("value")) {
                        return (Integer) fraisMap.get("value");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    private byte[] generateBarcode(String text) {
        try {
            text = sanitizeBarcodeText(text);
            text = truncateText(text, 80);
            BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.CODE_128, 200, 60); // Adjust dimensions
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            return pngOutputStream.toByteArray();
        } catch (WriterException | IOException e) {
            throw new RuntimeException("Failed to generate barcode", e);
        }
    }

    private String sanitizeBarcodeText(String text) {
        return text.replaceAll("[^\\x20-\\x7E]", "");
    }

    private String truncateText(String text, int maxLength) {
        if (text.length() > maxLength) {
            return text.substring(0, maxLength);
        } else {
            return text;
        }
    }

    private String formatBarcodeText(String text) {
        // pour un espace de 4 caracteres separees
        StringBuilder formattedText = new StringBuilder();
        int length = Math.min(text.length(), 12);
        for (int i = 0; i < length; i += 4) {
            if (i > 0) {
                formattedText.append(" ");
            }
            formattedText.append(text, i, Math.min(i + 4, length));
        }
        return formattedText.toString();
    }


    @Transactional
    public ColisDTO generateLabel(Long colisId) throws IOException {
        Optional<Colis> colisOptional = colisRepository.findById(colisId);
        if (colisOptional.isPresent()) {
            Colis colis = colisOptional.get();
            String barcodeText = colis.getCodeBarre();
            logger.info("Barcode Text: {}", barcodeText);


            logger.info("Nom du client: {}", colis.getClient().getNomClient());
            logger.info("Nom du destinataire: {}", colis.getNomDestinataire());
            logger.info("Adresse: {}", colis.getAdresseDestinataire());
            logger.info("Téléphone: {}", colis.getTelDestinataire());

            byte[] barcodeImageBytes = generateBarcode(barcodeText);
            Path tempFile = Files.createTempFile("barcode", ".png");
            Files.write(tempFile, barcodeImageBytes);

            String userHome = System.getProperty("user.home");
            String downloadDirectory = userHome + File.separator + "Downloads";
            File directory = new File(downloadDirectory);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String filePath = downloadDirectory + File.separator + "label_" + colis.getId() + ".pdf";
            PDRectangle pageSize = new PDRectangle(288, 144);
            try (PDDocument document = new PDDocument()) {
                PDPage page = new PDPage(pageSize);
                document.addPage(page);
                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {

                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                    contentStream.newLineAtOffset(20, 130);
                    contentStream.showText("Nom du client: " + (colis.getClient().getNomClient() != null ? colis.getClient().getNomClient() : ""));
                    contentStream.newLineAtOffset(0, -15);
                    contentStream.showText("Nom du destinataire: " + (colis.getNomDestinataire() != null ? colis.getNomDestinataire() : ""));
                    contentStream.newLineAtOffset(0, -15);
                    contentStream.showText("Adresse: " + (colis.getAdresseDestinataire() != null ? colis.getAdresseDestinataire() : ""));
                    contentStream.newLineAtOffset(0, -15);
                    contentStream.showText("Téléphone: " + (colis.getTelDestinataire() != null ? colis.getTelDestinataire() : ""));
                    contentStream.endText();


                    PDImageXObject pdImage = PDImageXObject.createFromFile(tempFile.toString(), document);
                    contentStream.drawImage(pdImage, 20, 40, 150, 40);


                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.HELVETICA, 10);
                    contentStream.newLineAtOffset(40, 30);
                    contentStream.showText(formatBarcodeText(barcodeText));
                    contentStream.endText();
                }
                document.save(filePath);
                Files.delete(tempFile);
            } catch (Exception e) {
                throw new IOException("Échec de la génération de l'étiquette", e);
            }
            return colisMapper.toDTO(colis);
        } else {
            throw new IOException("Colis non trouvé pour l'ID: " + colisId);
        }
    }

    public ResponseEntity<Resource> downloadLabel(Long id) throws IOException {

        Optional<Colis> colisOptional = colisRepository.findById(id);
        if (colisOptional.isPresent()) {
            Colis colis = colisOptional.get();
            if (!"CONFIRMER".equals(colis.getStatusColis())) {
                colis.setStatusColis(StatutColis.CONFIRMER);
                colisRepository.save(colis);
            }
        } else {
            throw new IOException("Colis non trouvé pour l'ID: " + id);
        }


        String userHome = System.getProperty("user.home");
        String downloadDirectory = userHome + File.separator + "Downloads";
        String filePath = downloadDirectory + File.separator + "label_" + id + ".pdf";
        Path path = Paths.get(filePath);
        if (Files.exists(path)) {
            Resource resource = new UrlResource(path.toUri());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"label_" + id + ".pdf\"")
                    .body(resource);
        } else {
            throw new IOException("File not found: " + filePath);
        }
    }
  /*  @Transactional
    public ColisDTO saveColis(ColisDTO colisDTO) {
        Colis colis = colisMapper.toEntity(colisDTO);
        colis.setStatusColis(StatutColis.BROUILLON);
        colis.setCreationDate(LocalDateTime.now());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Client client = user.getClient();
        colis.setClient(client);

        if (colisDTO.getServices() != null && !colisDTO.getServices().isEmpty()) {
            List<ServiceALivraison> selectedServices = colisDTO.getServices().stream()
                    .map(serviceALivraisonMapper::toEntity)
                    .collect(Collectors.toList());
            colis.setServices(selectedServices);
        }

        if (colis.getCodeBarre() == null) {
            colis.setCodeBarre(barcodeGenerator.generateUniqueBarcode());
        }


        Colis savedColis = colisRepository.save(colis);


        Integer frais = getFraisFromCamunda(savedColis.getId());
        if (frais != null) {
            savedColis.setFrais(frais);
        } else {
            throw new RuntimeException("Failed to retrieve frais from Camunda");
        }


        savedColis = colisRepository.save(savedColis);


        String successUrl = "http://localhost:9090/payment/success";
        String cancelUrl = "http://localhost:9090/payment/cancel";
        try {
            String paymentLink = stripeService.createCheckoutSession(frais, successUrl, cancelUrl);
            savedColis.setPaymentLink(paymentLink);
            savedColis = colisRepository.save(savedColis);
        } catch (StripeException e) {
            throw new RuntimeException("Erreur lors de la génération du lien de paiement Stripe", e);
        }

        return colisMapper.toDTO(savedColis);
    }

   */

    public ColisDTO saveColis(ColisDTO colisDTO) {
        Colis colis = colisMapper.toEntity(colisDTO);
        colis.setStatusColis(colisDTO.getStatusColis());
        colis.setTypeLivraison(colisDTO.getTypeLivraison());
        colis.setCreationDate(LocalDateTime.now());
        colis.setEstPaye(false);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Client client = new Client();
        colis.setClient(user.getClient());
        if (colisDTO.getServices() != null && !colisDTO.getServices().isEmpty()) {
            List<ServiceALivraison> selectedServices = colisDTO.getServices().stream()
                    .map(serviceALivraisonMapper::toEntity)
                    .collect(Collectors.toList());
            colis.setServices(selectedServices);
        }
        Integer frais = getFraisFromCamunda(colisDTO.getId());
        if (frais != null) {
            colis.setFrais(frais);
        }
        if (colis.getCodeBarre() == null) {
            colis.setCodeBarre(barcodeGenerator.generateUniqueBarcode());
        }
        if (colis.getPaymentToken() == null) {
            colis.setPaymentToken(tokenService.generatePaymentToken());
        }

        Colis savedColis = colisRepository.save(colis);
        if (savedColis.getNumeroColis() == null) {
            savedColis.setNumeroColis(String.format("Colis/%06d", savedColis.getId()));
            savedColis = colisRepository.save(savedColis);
        }
        return colisMapper.toDTO(savedColis);
    }
    @Transactional(readOnly = true)

    public ColisDTO getColisByNumeroColis(String numeroColis) {
        Optional<Colis> colisOptional = colisRepository.findByNumeroColis(numeroColis);
        if (colisOptional.isPresent()) {
            return colisMapper.toDTO(colisOptional.get());
        } else {
            throw new RuntimeException("Colis not found with numeroColis: " + numeroColis);
        }
    }

   /* @Transactional
    public Map<String, Object> createColisEnMasse(MultipartFile file) {
        List<Colis> colisList = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();
        List<String> logMessages = new ArrayList<>();
        int rejectedCount = 0;
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withDelimiter(';').withFirstRecordAsHeader().withTrim())) {
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Client client = user.getClient();
            for (CSVRecord csvRecord : csvRecords) {
                Colis colis = new Colis();
                try {
                    Ville villeDepart = findByVille(csvRecord.get("villeDepart"));
                    Ville villeDestinataire = findByVille(csvRecord.get("villeDestinataire"));
                    if (villeDepart == null) {
                        String errorMessage = "Ville de départ incorrecte (" + csvRecord.get("villeDepart") + ") pour la ligne " + csvRecord.getRecordNumber();
                        errorMessages.add("Ville non trouvée: " + csvRecord.get("villeDepart"));
                        errorMessages.add(errorMessage);
                        logger.warn(errorMessage);
                        rejectedCount++;
                        continue;
                    }
                    if (villeDestinataire == null) {
                        String errorMessage = "Ville de destination incorrecte (" + csvRecord.get("villeDestinataire") + ") pour la ligne " + csvRecord.getRecordNumber();
                        errorMessages.add("Ville non trouvée: " + csvRecord.get("villeDestinataire"));
                        errorMessages.add(errorMessage);
                        logger.warn(errorMessage);
                        rejectedCount++;
                        continue;
                    }
                    colis.setTypeLivraison(TypeLivraison.valueOf(csvRecord.get("typeLivraison")));
                    colis.setNomDestinataire(csvRecord.get("nomDestinataire"));
                    colis.setTelDestinataire(csvRecord.get("telDestinataire"));
                    colis.setAdresseDestinataire(csvRecord.get("adresseDestinataire"));
                    colis.setAdresse2Destinataire(csvRecord.get("adresse2Destinataire"));
                    colis.setVilleDestinataire(villeDestinataire);
                    colis.setVilleDepart(villeDepart);
                    colis.setNumTelChargeur(csvRecord.get("numTelChargeur"));
                    colis.setPremiereAdresseChargeur(csvRecord.get("premiereAdresseChargeur"));
                    colis.setDeuxiemeAdresseChargeur(csvRecord.get("deuxiemeAdresseChargeur"));
                    colis.setCashDelivery(parseDouble(csvRecord.get("cashDelivery")));
                    colis.setValeurColis(parseDouble(csvRecord.get("valeurColis")));
                    colis.setPoids(parseDouble(csvRecord.get("poids")));
                    colis.setLongueur(parseDouble(csvRecord.get("longueur")));
                    colis.setLargeur(parseDouble(csvRecord.get("largeur")));
                    colis.setHauteur(parseDouble(csvRecord.get("hauteur")));
                    colis.setDescription(csvRecord.get("description"));
                    colis.setCreationDate(LocalDateTime.now());
                    colis.setStatusColis(StatutColis.BROUILLON);
                    colis.setEstPaye(false);
                    colis.setClient(client);
                    colis.setPaymentToken(tokenService.generatePaymentToken());
                    Integer frais = camundaService.getFraisFromCamunda(colis.getTypeLivraison(), colis.getVilleDepart().getId(), colis.getVilleDestinataire().getId());
                    if (frais != null) {
                        colis.setFrais(frais);
                    }
                    colisList.add(colis);
                } catch (Exception e) {
                    String errorMessage = "Erreur lors du traitement de la ligne: " + csvRecord.getRecordNumber();
                    logger.error(errorMessage, e);
                    errorMessages.add(errorMessage);
                    rejectedCount++;
                }
            }
            List<Colis> savedColisList = colisRepository.saveAll(colisList);
            for (Colis savedColis : savedColisList) {
                savedColis.setCodeBarre(barcodeGenerator.generateUniqueBarcode());
                colisRepository.save(savedColis);
            }
            String logMessage = "Nombre total de colis à sauvegarder: " + savedColisList.size();
            logger.info(logMessage);
            logMessages.add(logMessage);
            Map<String, Object> response = new HashMap<>();
            response.put("errorMessages", errorMessages);
            response.put("logMessages", logMessages);
            response.put("colisCount", savedColisList.size());
            response.put("rejectedCount", rejectedCount);
            logger.info("Returning response: " + response);
            return response;
        } catch (IOException e) {
            logger.error("Erreur lors de la lecture du fichier CSV", e);
            throw new RuntimeException("Erreur lors de la lecture du fichier CSV", e);
        }
    }


    private Ville findByVille(String villeName) {
        Ville ville = villeRepository.findByVille(villeName);
        if (ville == null) {
            String errorMessage = "Ville non trouvée: " + villeName;
            logger.warn(errorMessage);
        }
        return ville;
    }

    */


    @Transactional
    public Map<String, Object> createColisEnMasse(MultipartFile file) {
        List<Colis> colisList = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();
        List<String> logMessages = new ArrayList<>();
        int rejectedCount = 0;
        String groupPaymentToken = tokenService.generatePaymentToken(); // Générer le token de paiement groupé

        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withDelimiter(';').withFirstRecordAsHeader().withTrim())) {
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Client client = user.getClient();

            for (CSVRecord csvRecord : csvRecords) {
                Colis colis = new Colis();
                try {
                    Ville villeDepart = findByVille(csvRecord.get("villeDepart"));
                    Ville villeDestinataire = findByVille(csvRecord.get("villeDestinataire"));
                    if (villeDepart == null) {
                        String errorMessage = "Ville de départ incorrecte (" + csvRecord.get("villeDepart") + ") pour la ligne " + csvRecord.getRecordNumber();
                        errorMessages.add("Ville non trouvée: " + csvRecord.get("villeDepart"));
                        errorMessages.add(errorMessage);
                        logger.warn(errorMessage);
                        rejectedCount++;
                        continue;
                    }
                    if (villeDestinataire == null) {
                        String errorMessage = "Ville de destination incorrecte (" + csvRecord.get("villeDestinataire") + ") pour la ligne " + csvRecord.getRecordNumber();
                        errorMessages.add("Ville non trouvée: " + csvRecord.get("villeDestinataire"));
                        errorMessages.add(errorMessage);
                        logger.warn(errorMessage);
                        rejectedCount++;
                        continue;
                    }
                    colis.setTypeLivraison(TypeLivraison.valueOf(csvRecord.get("typeLivraison")));
                    colis.setNomDestinataire(csvRecord.get("nomDestinataire"));
                    colis.setTelDestinataire(csvRecord.get("telDestinataire"));
                    colis.setAdresseDestinataire(csvRecord.get("adresseDestinataire"));
                    colis.setAdresse2Destinataire(csvRecord.get("adresse2Destinataire"));
                    colis.setVilleDestinataire(villeDestinataire);
                    colis.setVilleDepart(villeDepart);
                    colis.setNumTelChargeur(csvRecord.get("numTelChargeur"));
                    colis.setPremiereAdresseChargeur(csvRecord.get("premiereAdresseChargeur"));
                    colis.setDeuxiemeAdresseChargeur(csvRecord.get("deuxiemeAdresseChargeur"));
                    colis.setCashDelivery(parseDouble(csvRecord.get("cashDelivery")));
                    colis.setValeurColis(parseDouble(csvRecord.get("valeurColis")));
                    colis.setPoids(parseDouble(csvRecord.get("poids")));
                    colis.setLongueur(parseDouble(csvRecord.get("longueur")));
                    colis.setLargeur(parseDouble(csvRecord.get("largeur")));
                    colis.setHauteur(parseDouble(csvRecord.get("hauteur")));
                    colis.setDescription(csvRecord.get("description"));
                    colis.setCreationDate(LocalDateTime.now());
                    colis.setStatusColis(StatutColis.BROUILLON);
                    colis.setEstPaye(false);
                    colis.setClient(client);
                    colis.setPaymentToken(tokenService.generatePaymentToken());
                    colis.setGroupPaymentToken(groupPaymentToken);// Assigner le token de paiement groupé

                    Integer frais = camundaService.getFraisFromCamunda(colis.getTypeLivraison(), colis.getVilleDepart().getId(), colis.getVilleDestinataire().getId());
                    if (frais != null) {
                        colis.setFrais(frais);
                    }
                    colisList.add(colis);
                } catch (Exception e) {
                    String errorMessage = "Erreur lors du traitement de la ligne: " + csvRecord.getRecordNumber();
                    logger.error(errorMessage, e);
                    errorMessages.add(errorMessage);
                    rejectedCount++;
                }
            }

            List<Colis> savedColisList = colisRepository.saveAll(colisList);
            for (Colis savedColis : savedColisList) {
                savedColis.setCodeBarre(barcodeGenerator.generateUniqueBarcode());
                colisRepository.save(savedColis);
            }

            String logMessage = "Nombre total de colis à sauvegarder: " + savedColisList.size();
            logger.info(logMessage);
            logMessages.add(logMessage);
            Map<String, Object> response = new HashMap<>();
            response.put("errorMessages", errorMessages);
            response.put("logMessages", logMessages);
            response.put("colisCount", savedColisList.size());
            response.put("rejectedCount", rejectedCount);
            response.put("groupPaymentToken", groupPaymentToken);
            logger.info("Returning response: " + response);
            return response;
        } catch (IOException e) {
            logger.error("Erreur lors de la lecture du fichier CSV", e);
            throw new RuntimeException("Erreur lors de la lecture du fichier CSV", e);
        }
    }

    private Ville findByVille(String villeName) {
        Ville ville = villeRepository.findByVille(villeName);
        if (ville == null) {
            String errorMessage = "Ville non trouvée: " + villeName;
            logger.warn(errorMessage);
        }
        return ville;
    }


    @Transactional(readOnly = true)
    public Double getFraisByGroupPaymentToken(String groupPaymentToken) {
        List<Colis> colisList = colisRepository.findByGroupPaymentToken(groupPaymentToken);
        if (!colisList.isEmpty()) {
            return colisList.stream().mapToDouble(Colis::getFrais).sum();
        } else {
            throw new RuntimeException("No colis found for group payment token: " + groupPaymentToken);
        }
    }
    @Transactional
    public void updatePaymentStatusByGroupToken(String groupPaymentToken) {
        List<Colis> colisList = colisRepository.findByGroupPaymentToken(groupPaymentToken);
        if (!colisList.isEmpty()) {
            for (Colis colis : colisList) {
                colis.setEstPaye(true);
                colisRepository.save(colis);
            }
            String message = "Paiement effectué avec succès pour le token groupé " + groupPaymentToken;
            notificationService.createNotification(message);
        } else {
            throw new RuntimeException("No colis found for group payment token: " + groupPaymentToken);
        }
    }

    private Double parseDouble(String value) {
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }


    @Transactional(readOnly = true)
    public ColisDTO getColisByCodeBarre(String codeBarre) {
        logger.debug("Searching for Colis with codeBarre: {}", codeBarre);
        Optional<Colis> colisOptional = colisRepository.findByCodeBarre(codeBarre);
        if (colisOptional.isPresent()) {
            logger.debug("Found Colis with codeBarre: {}", codeBarre);
            return colisMapper.toDTO(colisOptional.get());
        } else {
            logger.error("Colis not found with codeBarre: {}", codeBarre);
            throw new RuntimeException("Colis not found with codeBarre: " + codeBarre);
        }
    }

    @Transactional
    public void updateColisStatusToConfirmed(Long colisId) {
        Optional<Colis> colisOptional = colisRepository.findById(colisId);
        if (colisOptional.isPresent()) {
            Colis colis = colisOptional.get();
            logger.info("Current status of colis with ID {}: {}", colisId, colis.getStatusColis());
            if (colis.getStatusColis() == StatutColis.BROUILLON && colis.getStatusColis() != StatutColis.ANNULER) {
                colis.setStatusColis(StatutColis.CONFIRMER);
                colisRepository.save(colis);
                logger.info("Updated colis with ID: {} to CONFIRMER", colisId);
            } else {
                logger.warn("Colis with ID: {} is not in BROUILLON status or is already CONFIRMER", colisId);
            }
        } else {
            logger.warn("Colis not found for ID: {}", colisId);
        }
    }




    @Transactional
    public void updateColisStatusToCancel(Long colisId) {
        Optional<Colis> colisOptional = colisRepository.findById(colisId);
        if (colisOptional.isPresent() ) {
            Colis colis = colisOptional.get();
            if (colis.getStatusColis() == StatutColis.BROUILLON && colis.getStatusColis()!=StatutColis.CONFIRMER) {
                colis.setStatusColis(StatutColis.ANNULER);
                colisRepository.save(colis);
                logger.info("Updated colis with ID: {} to ANNULER", colisId);
            }
        } else {
            logger.warn("Colis not found for ID: {}", colisId);
        }
    }




    public ByteArrayOutputStream generateMultipleLabelsPDF(List<Long> colisIds) throws IOException {
        PDDocument document = new PDDocument();
        for (Long colisId : colisIds) {
            ColisDTO colis = getColisById(colisId);
            if (colis != null && colis.getStatusColis() != StatutColis.ANNULER) {
                PDPage page = new PDPage(new PDRectangle(288, 144));
                document.addPage(page);
                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    addLabelToPage(contentStream, document, colis);
                }

            }

        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        document.save(out);
        document.close();
        return out;
    }


    private void addLabelToPage(PDPageContentStream contentStream, PDDocument document, ColisDTO colis) throws IOException {
        String barcodeText = colis.getCodeBarre();
        byte[] barcodeImageBytes = generateBarcode(barcodeText);
        Path tempFile = Files.createTempFile("barcode", ".png");
        Files.write(tempFile, barcodeImageBytes);

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        contentStream.newLineAtOffset(20, 130);
        contentStream.showText("Nom du client: " + (colis.getClient().getNomClient() != null ? colis.getClient().getNomClient() : ""));
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText("Nom du destinataire: " + (colis.getNomDestinataire() != null ? colis.getNomDestinataire() : ""));
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText("Adresse: " + (colis.getAdresseDestinataire() != null ? colis.getAdresseDestinataire() : ""));
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText("Téléphone: " + (colis.getTelDestinataire() != null ? colis.getTelDestinataire() : ""));
        contentStream.endText();

        PDImageXObject pdImage = PDImageXObject.createFromFile(tempFile.toString(), document);
        contentStream.drawImage(pdImage, 20, 40, 150, 40);

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 10);
        contentStream.newLineAtOffset(40, 30);
        contentStream.showText(formatBarcodeText(barcodeText));
        contentStream.endText();

        Files.delete(tempFile);
    }

    public List<ColisDTO> filterColis(List<StatutColis> statutColisList) {
        return colisRepository.findByStatusColisIn(statutColisList).stream()
                .map(colisMapper::toDTO)
                .collect(Collectors.toList());
    }

    public ColisDTO getColisByBarcode(String barcode) {
        Colis colis = colisRepository.findByCodeBarre(barcode)
                .orElseThrow(() -> new RuntimeException("Colis not found with barcode " + barcode));
        return colisMapper.toDTO(colis);
    }


    @Transactional
    public ByteArrayOutputStream generateConfirmationPDF(Long id) throws IOException {
        ColisDTO colisDTO = getColisById(id);
        Colis colis = colisMapper.toEntity(colisDTO);
        return createConfirmationPDF(colis);
    }

    private ByteArrayOutputStream createConfirmationPDF(Colis colis) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        // Set background color to white
        contentStream.setNonStrokingColor(255, 255, 255); // White background
        contentStream.addRect(0, 0, page.getMediaBox().getWidth(), page.getMediaBox().getHeight());
        contentStream.fill();

        // Header
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 24);
        contentStream.setNonStrokingColor(0, 0, 0); // Black color
        contentStream.newLineAtOffset(50, 750);
        contentStream.showText("Nous vous remercions pour la création de votre colis!");
        contentStream.endText();

        // Numéro de commande
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 14);
        contentStream.setNonStrokingColor(0, 0, 0); // Black color
        contentStream.newLineAtOffset(50, 700);
        contentStream.showText("Numéro de commande:");
        contentStream.endText();

        // Payment Token
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
        contentStream.setNonStrokingColor(0, 0, 0); // Black color
        contentStream.newLineAtOffset(50, 675);
        contentStream.showText(colis.getPaymentToken());
        contentStream.endText();

        // Instructions
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 14);
        contentStream.setNonStrokingColor(0, 0, 0); // Black color
        contentStream.newLineAtOffset(50, 630);
        contentStream.showText("Pour payer la création de votre colis, veuillez présenter ce numéro à l'agence CashPlus");
        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("la plus proche de chez vous. Une fois le paiement effectué, vous recevrez une confirmation de paiement.");
        contentStream.endText();

        // Contact Information
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
        contentStream.setNonStrokingColor(0, 0, 0); // Black color
        contentStream.newLineAtOffset(50, 580);
        contentStream.showText("Contactez-nous");
        contentStream.endText();

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 12);
        contentStream.setNonStrokingColor(0, 0, 0); // Black color
        contentStream.newLineAtOffset(50, 560);
        contentStream.showText("Nous vous répondrons au plus vite !");
        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("Email : contact@tawssil.ma");
        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("Téléphone : 06 91 56 49 60");
        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("Adresse : 1 rue des Pléiades, Angle Bd Abdelmoumen, Casablanca, Maroc");
        contentStream.endText();

        contentStream.close();
        document.save(out);
        document.close();

        return out;
    }



    @Transactional(readOnly = true)
    public Double getFraisByPaymentToken(String paymentToken) {
        Optional<Colis> colisOptional = colisRepository.findByPaymentToken(paymentToken);
        if (colisOptional.isPresent()) {
            return colisOptional.get().getFrais();
        } else {
            throw new RuntimeException("Colis not found with payment token: " + paymentToken);
        }
    }




    @Transactional
    public void updatePaymentStatus(String paymentToken) {
        Optional<Colis> colisOptional = colisRepository.findByPaymentToken(paymentToken);
        if (colisOptional.isPresent()) {
            Colis colis = colisOptional.get();
            colis.setEstPaye(true);
            colisRepository.save(colis);

            // Envoyer une notification après la mise à jour du statut de paiement
            String message = "Paiement effectué avec succès pour le token " + paymentToken;
            notificationService.createNotification(message);
        } else {
            throw new RuntimeException("Colis not found for payment token: " + paymentToken);
        }
    }
    private void sendPaymentNotification(String paymentToken, double frais) {
        String notificationUrl = "http://localhost:9090/api/colis/payment-notification";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> notificationRequest = new HashMap<>();
        notificationRequest.put("paymentToken", paymentToken);
        notificationRequest.put("message", "Le paiement pour le token " + paymentToken + " a été effectué avec succès. Le montant est: " + frais + " DH.");

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(notificationRequest, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(notificationUrl, entity, String.class);
            System.out.println("Notification sent: " + response.getBody());
        } catch (Exception e) {
            System.err.println("Error sending payment notification: " + e.getMessage());
        }
    }

    public List<ColisDTO> getPaidColis() {
        List<Colis> paidColis = colisRepository.findByEstPaye(true);
        return paidColis.stream()
                .map(colisMapper::toDTO)
                .collect(Collectors.toList());
    }


    public Double getTotalFrais() {
        List<Colis> colisList = colisRepository.findAll();
        return colisList.stream().mapToDouble(Colis::getFrais).sum();
    }

    public Double getTotalCrbt() {
        List<Colis> colisList = colisRepository.findAll();
        return colisList.stream().mapToDouble(Colis::getCashDelivery).sum();
    }


    public ByteArrayOutputStream generateGroupConfirmationPDF(String groupPaymentToken) throws IOException {
        List<Colis> colisList = colisRepository.findByGroupPaymentToken(groupPaymentToken);

        if (colisList.isEmpty()) {
            throw new RuntimeException("No colis found for group payment token: " + groupPaymentToken);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        contentStream.setNonStrokingColor(255, 255, 255); // White background
        contentStream.addRect(0, 0, page.getMediaBox().getWidth(), page.getMediaBox().getHeight());
        contentStream.fill();

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 24);
        contentStream.setNonStrokingColor(0, 0, 0); // Black color
        contentStream.newLineAtOffset(50, 750);
        contentStream.showText("Nous vous remercions pour la création de vos colis!");
        contentStream.endText();

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 14);
        contentStream.setNonStrokingColor(0, 0, 0); // Black color
        contentStream.newLineAtOffset(50, 700);
        contentStream.showText("Numéro de votre commande:");
        contentStream.endText();

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
        contentStream.setNonStrokingColor(0, 0, 0); // Black color
        contentStream.newLineAtOffset(50, 675);
        contentStream.showText(groupPaymentToken);
        contentStream.endText();

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 14);
        contentStream.setNonStrokingColor(0, 0, 0); // Black color
        contentStream.newLineAtOffset(50, 630);
        contentStream.showText("Pour payer la création de vos colis, veuillez présenter ce numéro à l'agence CashPlus");
        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("la plus proche de chez vous. Une fois le paiement effectué, vous recevrez une confirmation de paiement.");
        contentStream.endText();

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
        contentStream.setNonStrokingColor(0, 0, 0); // Black color
        contentStream.newLineAtOffset(50, 580);
        contentStream.showText("Contactez-nous");
        contentStream.endText();

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 12);
        contentStream.setNonStrokingColor(0, 0, 0); // Black color
        contentStream.newLineAtOffset(50, 560);
        contentStream.showText("Nous vous répondrons au plus vite !");
        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("Email : contact@tawssil.ma");
        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("Téléphone : 06 91 56 49 60");
        contentStream.newLineAtOffset(0, -20);
        contentStream.showText("Adresse : 1 rue des Pléiades, Angle Bd Abdelmoumen, Casablanca, Maroc");
        contentStream.endText();

        contentStream.close();
        document.save(out);
        document.close();

        return out;
    }



}
