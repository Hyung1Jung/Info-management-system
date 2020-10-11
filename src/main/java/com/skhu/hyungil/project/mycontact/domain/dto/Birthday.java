package com.skhu.hyungil.project.mycontact.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDate;

@Embeddable // 엔티티에 속해있는 Dto
@NoArgsConstructor
@Data // 해쉬 값을 toString으로
public class Birthday {
    private Integer yearOfBirthday;

    @Min(1)
    @Max(12)
    private Integer monthOfBirthday;
    @Min(1)
    @Max(31)
    private Integer dayOfBirthday;

    public Birthday(LocalDate birthday) {
        this.yearOfBirthday = birthday.getYear();
        this.monthOfBirthday = birthday.getMonthValue();
        this.dayOfBirthday = birthday.getDayOfMonth();

    }
}

