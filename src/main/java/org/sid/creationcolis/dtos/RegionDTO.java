package org.sid.creationcolis.dtos;

import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.sid.creationcolis.enums.Zone;

@Getter
@Setter

public class RegionDTO {
    private Long id;
    private String region;
  //  private Zone zone;

}
