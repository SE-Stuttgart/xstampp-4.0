package de.xstampp.service.project.service.data.XStamppProject;

import de.xstampp.service.project.data.entity.Project;
import de.xstampp.service.project.data.entity.SystemDescription;
import de.xstampp.service.project.service.dao.control_structure.iface.*;
import de.xstampp.service.project.service.dao.iface.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class XStamppProjectService {

    /*
     * Current Version: Flyway-Database-History 2.1
     */

    private XStamppProjectCopyMapper copyService;

    private IProjectDAO projectDAO;

    private ISystemDescriptionDAO systemDescriptionDAO;
    private ILossDAO lossDAO;
    private IHazardDAO hazardDAO;
    private ISystemConstraintDAO systemConstraintDAO;
    private ISubHazardDAO subHazardDAO;
    private ISubSystemConstraintDAO subSystemConstraintDAO;

    private IControllerDAO controllerDAO;
    private IActuatorDAO actuatorDAO;
    private ISensorDAO sensorDAO;
    private IControlledProcessDAO controlledProcessDAO;

    private IControlActionDAO controlActionDAO;
    private IFeedbackDAO feedbackDAO;
    private IInputDAO inputDAO;
    private IOutputDAO outputDAO;
    private IArrowDAO arrowDAO;
    private IBoxDAO boxDAO;
    private IResponsibilityDAO responsibilityDAO;

    private IUnsafeControlActionDAO unsafeControlActionDAO;
    private IControllerConstraintDAO controllerConstraintDAO;

    private IProcessModelDAO processModelDAO;
    private IProcessVariableDAO processVariableDAO;
    private IDiscreteProcessVariableValueDAO discreteProcessVariableValueDAO;
    private IRuleDAO ruleDAO;
    private IConversionDAO conversionDAO;
    private ILossScenarioDAO lossScenarioDAO;
    private IImplementationConstraintDAO implementationConstraintDAO;

    private IHazardLossLinkDAO hazardLossLinkDAO;
    private IHazardSystemConstraintLinkDAO hazardSystemConstraintLinkDAO;
    private IUnsafeControlActionHazardLinkDAO unsafeControlActionHazardLinkDAO;
    private IProcessModelProcessVariableLinkDAO processModelProcessVariableLinkDAO;
    private IUnsafeControlActionSubHazardLinkDAO unsafeControlActionSubHazardLinkDAO;
    private IProcessVariableResponsibilityLinkDAO processVariableResponsibilityLinkDAO;
    private IResponsibilitySystemConstraintLinkDAO responsibilitySystemConstraintLinkDAO;

    private ILastIdDAO lastIdDAO;

    @Autowired
    public XStamppProjectService(IProjectDAO projectDAO, ISystemDescriptionDAO systemDescriptionDAO, ILossDAO lossDAO,
                                 IHazardDAO hazardDAO, ISystemConstraintDAO systemConstraintDAO, ILastIdDAO lastIdDAO,
                                 IControllerDAO controllerDAO, IActuatorDAO actuatorDAO, ISensorDAO sensorDAO,
                                 IControlledProcessDAO controlledProcessDAO, IControlActionDAO controlActionDAO,
                                 IFeedbackDAO feedbackDAO, IInputDAO inputDAO, IOutputDAO outputDAO,
                                 IBoxDAO boxDAO, IResponsibilityDAO responsibilityDAO, IHazardLossLinkDAO hazardLossLinkDAO,
                                 IUnsafeControlActionDAO unsafeControlActionDAO, IProcessVariableDAO processVariableDAO,
                                 IControllerConstraintDAO controllerConstraintDAO, IProcessModelDAO processModelDAO,
                                 IDiscreteProcessVariableValueDAO discreteProcessVariableValueDAO,
                                 IConversionDAO conversionDAO, ILossScenarioDAO lossScenarioDAO, ISubHazardDAO subHazardDAO,
                                 IImplementationConstraintDAO implementationConstraintDAO, IRuleDAO ruleDAO,
                                 IHazardSystemConstraintLinkDAO hazardSystemConstraintLinkDAO, IArrowDAO arrowDAO,
                                 IUnsafeControlActionHazardLinkDAO unsafeControlActionHazardLinkDAO,
                                 IProcessModelProcessVariableLinkDAO processModelProcessVariableLinkDAO,
                                 IUnsafeControlActionSubHazardLinkDAO unsafeControlActionSubHazardLinkDAO,
                                 IProcessVariableResponsibilityLinkDAO processVariableResponsibilityLinkDAO,
                                 IResponsibilitySystemConstraintLinkDAO responsibilitySystemConstraintLinkDAO,
                                 ISubSystemConstraintDAO subSystemConstraintDAO, XStamppProjectCopyMapper copyService) {
        this.projectDAO = projectDAO;
        this.systemDescriptionDAO = systemDescriptionDAO;
        this.lossDAO = lossDAO;
        this.hazardDAO = hazardDAO;
        this.systemConstraintDAO = systemConstraintDAO;
        this.subHazardDAO = subHazardDAO;
        this.subSystemConstraintDAO = subSystemConstraintDAO;
        this.controllerDAO = controllerDAO;
        this.actuatorDAO = actuatorDAO;
        this.sensorDAO = sensorDAO;
        this.controlledProcessDAO = controlledProcessDAO;
        this.controlActionDAO = controlActionDAO;
        this.feedbackDAO = feedbackDAO;
        this.inputDAO = inputDAO;
        this.outputDAO = outputDAO;
        this.arrowDAO = arrowDAO;
        this.boxDAO = boxDAO;
        this.responsibilityDAO = responsibilityDAO;
        this.unsafeControlActionDAO = unsafeControlActionDAO;
        this.controllerConstraintDAO = controllerConstraintDAO;
        this.processModelDAO = processModelDAO;
        this.processVariableDAO = processVariableDAO;
        this.discreteProcessVariableValueDAO = discreteProcessVariableValueDAO;
        this.ruleDAO = ruleDAO;
        this.conversionDAO = conversionDAO;
        this.lossScenarioDAO = lossScenarioDAO;
        this.implementationConstraintDAO = implementationConstraintDAO;
        this.hazardLossLinkDAO = hazardLossLinkDAO;
        this.hazardSystemConstraintLinkDAO = hazardSystemConstraintLinkDAO;
        this.unsafeControlActionHazardLinkDAO = unsafeControlActionHazardLinkDAO;
        this.processModelProcessVariableLinkDAO = processModelProcessVariableLinkDAO;
        this.unsafeControlActionSubHazardLinkDAO = unsafeControlActionSubHazardLinkDAO;
        this.processVariableResponsibilityLinkDAO = processVariableResponsibilityLinkDAO;
        this.responsibilitySystemConstraintLinkDAO = responsibilitySystemConstraintLinkDAO;
        this.copyService = copyService;
        this.lastIdDAO = lastIdDAO;
    }

    /**
     * Get a XStamppProject with all related entities of a project
     *
     * @param projectUUID the id of the project
     * @return the XStamppProject
     */
    @Transactional
    public XStamppProject findXStamppProjectByProjectId(UUID projectUUID) {
        return XStamppProject.XStamppProjectBuilder
                .aXStamppProject()
                .withProjectUUID(projectDAO.findById(projectUUID, false).getId())
                .withSystemDescription(systemDescriptionDAO.findById(projectUUID, false))
                .withLosses(lossDAO.findAll(projectUUID))
                .withHazards(hazardDAO.findAll(projectUUID))
                .withSystemConstraints(systemConstraintDAO.findAll(projectUUID))
                .withSubHazards(subHazardDAO.findAll(projectUUID))
                .withSubSystemConstraints(subSystemConstraintDAO.findAll(projectUUID))
                .withControllers(controllerDAO.findAll(projectUUID))
                .withActuators(actuatorDAO.findAll(projectUUID))
                .withSensors(sensorDAO.findAll(projectUUID))
                .withControlledProcesses(controlledProcessDAO.findAll(projectUUID))
                .withControlActions(controlActionDAO.findAll(projectUUID))
                .withFeedback(feedbackDAO.findAll(projectUUID))
                .withInputs(inputDAO.findAll(projectUUID))
                .withOutputs(outputDAO.findAll(projectUUID))
                .withArrows(arrowDAO.findAll(projectUUID))
                .withBoxes(boxDAO.findAll(projectUUID))
                .withResponsibilities(responsibilityDAO.findAll(projectUUID))
                .withUnsafeControlActions(unsafeControlActionDAO.findAll(projectUUID))
                .withControllerConstraints(controllerConstraintDAO.findAll(projectUUID))
                .withProcessModels(processModelDAO.findAll(projectUUID))
                .withProcessVariables(processVariableDAO.findAll(projectUUID))
                .withDiscreteProcessVariableValues(discreteProcessVariableValueDAO.getAllValuesById(projectUUID))
                .withRules(ruleDAO.findAll(projectUUID))
                .withConversions(conversionDAO.findAll(projectUUID))
                .withLossScenarios(lossScenarioDAO.findAll(projectUUID))
                .withImplementationConstraints(implementationConstraintDAO.findAll(projectUUID))
                .withUnsafeControlActionHazardLinks(unsafeControlActionHazardLinkDAO.getAllLinks(projectUUID))
                .withUnsafeControlActionSubHazardLinks(unsafeControlActionSubHazardLinkDAO.getAllLinks(projectUUID))
                .withHazardLossLinks(hazardLossLinkDAO.getAllLinks(projectUUID))
                .withHazardSystemConstraintLinks(hazardSystemConstraintLinkDAO.getAllLinks(projectUUID))
                .withResponsibilitySystemConstraintLinks(responsibilitySystemConstraintLinkDAO.getAllLinks(projectUUID, false))
                .withProcessModelProcessVariableLinks(processModelProcessVariableLinkDAO.getAllLinks(projectUUID))
                .withProcessVariableResponsibilityLinks(processVariableResponsibilityLinkDAO.getAllLinks(projectUUID))
                .withProjectEntityLastIds(lastIdDAO.findAllProjectEntityLastIds(projectUUID))
                .withSubHazardLastIds(lastIdDAO.findAllSubHazardLastIds(projectUUID))
                .withSubSystemConstraintLastIds(lastIdDAO.findAllSubSystemConstraintLastIds(projectUUID))
                .withUnsafeControlActionLastIds(lastIdDAO.findAllUnsafeControlActionLastId(projectUUID))
                .withRuleLastIds(lastIdDAO.findAllRuleLastIds(projectUUID))
                .withConversionLastIds(lastIdDAO.findAllConversionLastIds(projectUUID))
                .build();
    }

    /**
     * Persist all entities of a XStamppProject into an existing project.
     *
     * @return the Project the entities got inserted
     */
    @Transactional
    public Project saveXStamppProject(XStamppProject xstamppProject) {

        systemDescriptionDAO.makePersistent(xstamppProject.getSystemDescription());

        lossDAO.saveAll(xstamppProject.getLosses());
        hazardDAO.saveAll(xstamppProject.getHazards());
        systemConstraintDAO.saveAll(xstamppProject.getSystemConstraints());
        subHazardDAO.saveAll(xstamppProject.getSubHazards());
        subSystemConstraintDAO.saveAll(xstamppProject.getSubSystemConstraints());

        controllerDAO.saveAll(xstamppProject.getControllers());
        actuatorDAO.saveAll(xstamppProject.getActuators());
        sensorDAO.saveAll(xstamppProject.getSensors());
        controlledProcessDAO.saveAll(xstamppProject.getControlledProcesses());
        controlActionDAO.saveAll(xstamppProject.getControlActions());
        feedbackDAO.saveAll(xstamppProject.getFeedback());
        inputDAO.saveAll(xstamppProject.getInputs());
        outputDAO.saveAll(xstamppProject.getOutputs());
        arrowDAO.saveAll(xstamppProject.getArrows());
        boxDAO.saveAll(xstamppProject.getBoxes());
        responsibilityDAO.saveAll(xstamppProject.getResponsibilities());

        unsafeControlActionDAO.saveAll(xstamppProject.getUnsafeControlActions());
        controllerConstraintDAO.saveAll(xstamppProject.getControllerConstraints());

        processModelDAO.saveAll(xstamppProject.getProcessModels());
        processVariableDAO.saveAll(xstamppProject.getProcessVariables());
        discreteProcessVariableValueDAO.saveAll(xstamppProject.getDiscreteProcessVariableValues());
        ruleDAO.saveAll(xstamppProject.getRules());
        conversionDAO.saveAll(xstamppProject.getConversions());
        lossScenarioDAO.saveAll(xstamppProject.getLossScenarios());
        implementationConstraintDAO.saveAll(xstamppProject.getImplementationConstraints());

        hazardLossLinkDAO.saveAll(xstamppProject.getHazardLossLinks());
        hazardSystemConstraintLinkDAO.saveAll(xstamppProject.getHazardSystemConstraintLinks());
        unsafeControlActionHazardLinkDAO.saveAll(xstamppProject.getUnsafeControlActionHazardLinks());
        processModelProcessVariableLinkDAO.saveAll(xstamppProject.getProcessModelProcessVariableLinks());
        unsafeControlActionSubHazardLinkDAO.saveAll(xstamppProject.getUnsafeControlActionSubHazardLinks());
        processVariableResponsibilityLinkDAO.saveAll(xstamppProject.getProcessVariableResponsibilityLinks());
        responsibilitySystemConstraintLinkDAO.saveAll(xstamppProject.getResponsibilitySystemConstraintLinks());

        lastIdDAO.saveAllProjectEntityLastIds(xstamppProject.getProjectEntityLastIds());
        lastIdDAO.saveAllSubHazardLastIds(xstamppProject.getSubHazardLastIds());
        lastIdDAO.saveAllSubSystemConstraintLastIds(xstamppProject.getSubSystemConstraintLastIds());
        lastIdDAO.saveAllUnsafeControlActionLastIds(xstamppProject.getUnsafeControlActionLastIds());
        lastIdDAO.saveAllRuleLastIds(xstamppProject.getRuleLastIds());
        lastIdDAO.saveAllConversionLastIds(xstamppProject.getConversionLastIds());

        return projectDAO.findById(xstamppProject.getProjectUUID(), false);
    }

    public XStamppProject setProjectUUID(UUID projectUUID, XStamppProject xstamppProject) {
        return XStamppProject.XStamppProjectBuilder
                .aXStamppProject()
                .withProjectUUID(projectUUID)
                .withSystemDescription(SystemDescription.SystemDescriptionBuilder
                        .aSystemDescription()
                        .from(xstamppProject.getSystemDescription())
                        .withProjectId(projectUUID)
                        .build())
                .withLosses(copyService.copyLosses(projectUUID, xstamppProject))
                .withHazards(copyService.copyHazards(projectUUID, xstamppProject))
                .withSystemConstraints(copyService.copySystemConstraints(projectUUID, xstamppProject))
                .withSubHazards(copyService.copySubHazards(projectUUID, xstamppProject))
                .withSubSystemConstraints(copyService.copySubSystemConstraints(projectUUID, xstamppProject))
                .withControllers(copyService.copyControllers(projectUUID, xstamppProject))
                .withActuators(copyService.copyActuators(projectUUID, xstamppProject))
                .withSensors(copyService.copySensors(projectUUID, xstamppProject))
                .withControlledProcesses(copyService.copyControlledProcesses(projectUUID, xstamppProject))
                .withControlActions(copyService.copyControlActions(projectUUID, xstamppProject))
                .withFeedback(copyService.copyFeedback(projectUUID, xstamppProject))
                .withInputs(copyService.copyInputs(projectUUID, xstamppProject))
                .withOutputs(copyService.copyOutputs(projectUUID, xstamppProject))
                .withArrows(copyService.copyArrows(projectUUID, xstamppProject))
                .withBoxes(copyService.copyBoxes(projectUUID, xstamppProject))
                .withResponsibilities(copyService.copyResponsibilities(projectUUID, xstamppProject))
                .withUnsafeControlActions(copyService.copyUnsafeControlActions(projectUUID, xstamppProject))
                .withControllerConstraints(copyService.copyControllerConstrains(projectUUID, xstamppProject))
                .withProcessModels(copyService.copyProcessModels(projectUUID, xstamppProject))
                .withProcessVariables(copyService.copyProcessVariables(projectUUID, xstamppProject))
                .withDiscreteProcessVariableValues(copyService.copyDiscreteProcessVariableValues(projectUUID, xstamppProject))
                .withRules(copyService.copyRules(projectUUID, xstamppProject))
                .withConversions(copyService.copyConversions(projectUUID, xstamppProject))
                .withLossScenarios(copyService.copyLossScenarios(projectUUID, xstamppProject))
                .withImplementationConstraints(copyService.copyImplementationConstraints(projectUUID, xstamppProject))
                .withUnsafeControlActionHazardLinks(copyService.copyUnsafeControlActionHazardLinks(projectUUID, xstamppProject))
                .withUnsafeControlActionSubHazardLinks(copyService.copyUnsafeControlActionSubHazardLinks(projectUUID, xstamppProject))
                .withHazardLossLinks(copyService.copyHazardLossLinks(projectUUID, xstamppProject))
                .withHazardSystemConstraintLinks(copyService.copyHazardSystemConstraintLinks(projectUUID, xstamppProject))
                .withResponsibilitySystemConstraintLinks(copyService.copyResponsibilitySystemConstraintLinks(projectUUID, xstamppProject))
                .withProcessModelProcessVariableLinks(copyService.copyProcessModelProcessVariableLinks(projectUUID, xstamppProject))
                .withProcessVariableResponsibilityLinks(copyService.copyProcessVariableResponsibilityLinks(projectUUID, xstamppProject))
                .withProjectEntityLastIds(copyService.copyProjectEntityLastIds(projectUUID, xstamppProject))
                .withSubHazardLastIds(copyService.copySubHazardLastIds(projectUUID, xstamppProject))
                .withSubSystemConstraintLastIds(copyService.copySubSystemConstraintLastIds(projectUUID, xstamppProject))
                .withUnsafeControlActionLastIds(copyService.copyUnsafeControlActionLastIds(projectUUID, xstamppProject))
                .withRuleLastIds(copyService.copyRuleLastIds(projectUUID, xstamppProject))
                .withConversionLastIds(copyService.copyConversionLastIds(projectUUID, xstamppProject))
                .build();
    }
}
