package br.com.fiap.gs.ConnectA.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SkillsSugeridasDTO {
    private List<String> skills;
    private String mensagem;
}