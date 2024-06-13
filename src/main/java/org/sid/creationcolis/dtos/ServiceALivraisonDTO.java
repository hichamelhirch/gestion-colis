package org.sid.creationcolis.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceALivraisonDTO {
    private Long id;
    private String serviceName;
    private String description;
    private Double price;
    private String status;
}
