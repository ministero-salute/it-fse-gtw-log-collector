package it.finanze.sanita.fse2.ms.gtw.logcollector.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.mapping.Field;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocalityDTO {

    @Field("raw_value")
    private String rawValue;

    @Field("asl_code")
    private String aslCode;

    @Field("structure")
    private String structure;

    /**
     * Decode locality basing on input information.
     *
     * @param locality The field to be decoded.
     * @return A DTO containing locality information.
     */
    public static LocalityDTO decodeLocality(String locality) {

        LocalityDTO out = new LocalityDTO();
        out.setRawValue(locality);

        if (StringUtils.isEmpty(locality)) {
            return out;
        }

        int lastCaretIndex = locality.lastIndexOf("^^^^");
        if (lastCaretIndex == -1) {
            return out;
        }

        String lastToken = locality.substring(lastCaretIndex + 4);

        if (lastToken.length() == 12) {
            out.setAslCode(lastToken.substring(3, 6));       
            out.setStructure(lastToken.substring(6, 12));    
        } else if (lastToken.length() == 6) {
            out.setAslCode(lastToken.substring(3, 6));       
        }

        return out;
    }
}
