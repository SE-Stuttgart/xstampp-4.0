package de.xstampp.service.project.data.dto.eclipse_project.link;

public enum LinkingType {

    UNSAFE_CONTROL_ACTION(false),
    HAZ_ACC_LINK(false),
    ACC_S0_LINK(false),
    HAZ_S0_LINK(false),
    DR_SC_LINK(false),
    DR0_SC_LINK(false),
    DR1_CSC_LINK(false),
    DR2_CausalSC_LINK(false),
    DR2_CausalScenarioSC_LINK(false),
    UCA_HAZ_LINK(false),
    UCA_CausalFactor_LINK(true),
    UcaCfLink_Component_LINK(true),
    CausalEntryLink_HAZ_LINK(true),
    CausalEntryLink_SC2_LINK(true),
    CausalEntryLink_ANCHOR(true),
    CausalHazLink_SC2_LINK(true),
    CausalEntryLink_Scenario_LINK(true),
    SC2_SC1_LINK(false);

    private boolean acceptNullLinks;

    private LinkingType(boolean acceptNullLinks) {
        this.acceptNullLinks = acceptNullLinks;
    }

    public boolean isAcceptingNullLinks() {
        return acceptNullLinks;
    }
}

