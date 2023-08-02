package com.mirrorview.domain.essay.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EssayListDto {

    private Long id;
    private String title;
    private List<EssayDetailDto> essayDetails = new ArrayList<>();

    public static EssayListDto createEssay(EssayDto essay, List<EssayDetailDto> essayDetail) {
        return EssayListDto.builder()
                .id(essay.getId())
                .title(essay.getTitle())
                .essayDetails(essayDetail)
                .build();
    }
}
