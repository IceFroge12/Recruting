package ua.kpi.nc.controller.admin;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.kpi.nc.persistence.dto.FormQuestionDto;
import ua.kpi.nc.persistence.model.*;
import ua.kpi.nc.persistence.model.impl.real.FormAnswerVariantImpl;
import ua.kpi.nc.persistence.model.impl.real.FormQuestionImpl;
import ua.kpi.nc.persistence.util.FormQuestionComparator;
import ua.kpi.nc.service.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ua.kpi.nc.persistence.model.enums.FormQuestionTypeEnum.*;

/**
 * Created by dima on 23.04.16.
 */
@RestController
@RequestMapping("/admin")
public class AdminFormSettingsController {

    private DecisionService decisionService = ServiceFactory.getDecisionService();
    private FormQuestionService formQuestionService = ServiceFactory.getFormQuestionService();
    private RoleService roleService = ServiceFactory.getRoleService();
    private QuestionTypeService questionTypeService = ServiceFactory.getQuestionTypeService();


    @RequestMapping(value = "getQuestions", method = RequestMethod.GET)
    public List<FormQuestionDto> getQuestions(@RequestParam String role) {
        Role roleTitle = roleService.getRoleByTitle(role);
        List<FormQuestion> formQuestionList = formQuestionService.getByRole(roleTitle);
        List<FormQuestionDto> formQuestionListDto = new ArrayList<>();
        for (FormQuestion formQuestion : formQuestionList) {
            FormQuestionDto formQuestionDto = new FormQuestionDto();
            formQuestionDto.setId(formQuestion.getId());
            formQuestionDto.setType(formQuestion.getQuestionType().getTypeTitle());
            formQuestionDto.setMandatory(formQuestion.isMandatory());
            formQuestionDto.setEnable(formQuestion.isEnable());
            formQuestionDto.setQuestion(formQuestion.getTitle());
            formQuestionDto.setOrder(formQuestion.getOrder());
            formQuestionDto.setFormAnswerVariants(new ArrayList<>());
            if (formQuestion.getFormAnswerVariants() == null) {
                formQuestion.setFormAnswerVariants(new ArrayList<>());
            }
            for (FormAnswerVariant answerVariant : formQuestion.getFormAnswerVariants()) {
                formQuestionDto.getFormAnswerVariants().add(answerVariant.getAnswer());
            }
            formQuestionListDto.add(formQuestionDto);
        }
        return formQuestionListDto;
    }

    @RequestMapping(value = "addQuestion", method = RequestMethod.POST)
    public ResponseEntity addQuestion(@RequestBody FormQuestionDto formQuestionDto) {
        Role role = roleService.getRoleByTitle(formQuestionDto.getRole());
        QuestionType questionType = questionTypeService.getQuestionTypeByName(formQuestionDto.getType());
        List<Role> roleList = new ArrayList<>();
        roleList.add(role);
        List<FormAnswerVariant> formAnswerVariantList = new ArrayList<>();
        for (String formAnswerVariantString : formQuestionDto.getFormAnswerVariants()) {
            FormAnswerVariant formAnswerVariant = new FormAnswerVariantImpl(formAnswerVariantString);
            formAnswerVariantList.add(formAnswerVariant);
        }
        FormQuestion formQuestion = new FormQuestionImpl();
        formQuestion.setTitle(formQuestionDto.getQuestion());
        formQuestion.setQuestionType(questionType);
        formQuestion.setEnable(formQuestionDto.isEnable());
        formQuestion.setMandatory(formQuestionDto.isMandatory());
        formQuestion.setFormAnswerVariants(formAnswerVariantList);
        formQuestion.setOrder(formQuestionDto.getOrder());
        formQuestionService.insertFormQuestion(formQuestion, role, formAnswerVariantList);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @RequestMapping(value = "updateAppFormQuestion", method = RequestMethod.POST)
    public void updateQuestions(@RequestBody FormQuestionDto formQuestionDto) {
        QuestionType questionType = questionTypeService.getQuestionTypeByName(formQuestionDto.getType());
        List<FormAnswerVariant> formAnswerVariantList = new ArrayList<>();
        if (questionType.getTypeTitle().equals(CHECKBOX.getTitle()) ||
                questionType.getTypeTitle().equals(SELECT.getTitle()) ||
                questionType.getTypeTitle().equals(RADIO.getTitle())) {
            for (String s : formQuestionDto.getFormAnswerVariants()) {
                FormAnswerVariant formAnswerVariant = new FormAnswerVariantImpl();
                formAnswerVariant.setAnswer(s);
                formAnswerVariantList.add(formAnswerVariant);
            }
        }
        FormQuestion formQuestion = new FormQuestionImpl();
        formQuestion.setId(formQuestionDto.getId());
        formQuestion.setTitle(formQuestionDto.getQuestion());
        formQuestion.setQuestionType(questionType);
        formQuestion.setFormAnswerVariants(formAnswerVariantList);
        formQuestion.setOrder(formQuestionDto.getOrder());
        formQuestion.setMandatory(formQuestionDto.isMandatory());
        formQuestion.setEnable(formQuestionDto.isEnable());
        formQuestionService.updateQuestions(formQuestion, formAnswerVariantList);
    }

    @RequestMapping(value = "getAllQuestionType", method = RequestMethod.GET)
    public List<QuestionType> getAllQuestionType() {
        return questionTypeService.getAllQuestionType();
    }

    @RequestMapping(value = "changeQuestionStatus", method = RequestMethod.GET)
    public boolean changeQuestionStatus(@RequestParam Long id) {
        FormQuestion formQuestion = formQuestionService.getById(id);
        formQuestion.setEnable(!formQuestion.isEnable());
        formQuestionService.updateFormQuestion(formQuestion);
        return formQuestion.isEnable();
    }

    @RequestMapping(value = "changeQuestionMandatoryStatus", method = RequestMethod.GET)
    public boolean changeQuestionMandatoryStatus(@RequestParam Long id) {
        FormQuestion formQuestion = formQuestionService.getById(id);
        formQuestion.setMandatory(!formQuestion.isMandatory());
        formQuestionService.updateFormQuestion(formQuestion);
        return formQuestion.isMandatory();
    }

    @RequestMapping(value = "getDecisionMatrix", method = RequestMethod.GET)
    public List<Decision> getDecisionMatrix() {
        return decisionService.getAll();
    }

    @RequestMapping(value = "saveDecisionMatrix", method = RequestMethod.POST)
    public ResponseEntity saveDecisionMatrix(@RequestBody List<Decision> decisionMatrix) {
        decisionService.updateDecisionMatrix(decisionMatrix);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @RequestMapping(value = "deleteQuestion", method = RequestMethod.GET)
    public ResponseEntity delete(@RequestParam Long id) {
        FormQuestion formQuestion = formQuestionService.getById(id);
        formQuestionService.deleteFormQuestion(formQuestion);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

}
