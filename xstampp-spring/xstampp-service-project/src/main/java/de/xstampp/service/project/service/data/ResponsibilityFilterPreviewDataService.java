package de.xstampp.service.project.service.data;

import de.xstampp.service.project.data.dto.ResponsibilityFilterPreviewResponseDTO;
import de.xstampp.service.project.data.entity.Responsibility;
import de.xstampp.service.project.data.entity.ResponsibilitySystemConstraintLink;
import de.xstampp.service.project.service.dao.iface.IResponsibilityDAO;
import de.xstampp.service.project.service.dao.iface.IResponsibilitySystemConstraintLinkDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This service is solely providing preview numbers specifically for a certain Responsibility filtering feature. See
 * {@link ResponsibilityFilterPreviewResponseDTO} for more information.
 */
@Service
@Transactional
public class ResponsibilityFilterPreviewDataService {

    @Autowired
    IResponsibilityDAO responsibilityData;

    @Autowired
    IResponsibilitySystemConstraintLinkDAO respoScLinkDAO;

    /**
     * This object is the result of {@link #countAndSearch(List, Function, Object, boolean)}. It contains a
     * {@link #countedTargetValues map} of how many times a target value occurred and a {@link #searchResult list} of
     * all elements whose target value matched a given value.
     *
     * @param <B> The type of elements in <i>baseList</i> and therefore also the type of the search results.
     * @param <T> Type of the target values retrieved from each element in <i>baseList</i>
     */
    private static class CountAndSearchResult<B, T> {

        /**
         * Has a key for each target value that occurred at least once in <i>baseList</i>. Its corresponding value
         * states how many times the target value occurred. If <i>onlySearching</i> was true, this value is null.
         * @see CountAndSearchResult
         */
        Map<T, Integer> countedTargetValues;
        /**
         * Contains all elements in <i>baseList</i> whose target value was equal to <i>targetValueToBeSearched</i>.
         * @see CountAndSearchResult
         */
        List<B> searchResult;

        private CountAndSearchResult(Map<T, Integer> countedTargetValues,
                                     List<B> searchResult) {
            this.countedTargetValues = countedTargetValues;
            this.searchResult = searchResult;
        }
    }

    /**
     * This object is the result of {@link #chainedCounting(List, Function, Object, Function, Function, boolean)}. It
     * contains the counting result maps from the {@link #firstCountedTargetValues first} and the
     * {@link #chainedCountedTargetValues second}
     * {@link #countAndSearch(List, Function, Object, boolean) countAndSearch}. Additionally, it contains the
     * {@link #searchResultSize number of elements} found during the search operation of the first countAndSearch.
     *
     * @param <T1> The type of the target values in the first countAndSearch
     * @param <T2> The type of the target values in the second countAndSearch
     */
    private static class ChainedCountingResult<T1, T2> {

        /**
         * The counting results from the fist countAndSearch
         * @see ChainedCountingResult
         */
        Map<T1, Integer> firstCountedTargetValues;
        /**
         * The counting results from the second countAndSearch
         * @see ChainedCountingResult
         */
        Map<T2, Integer> chainedCountedTargetValues;
        /**
         * The number of elements found during the search operation of the first countAndSearch.
         * @see ChainedCountingResult
         */
        int searchResultSize;

        private ChainedCountingResult(Map<T1, Integer> firstCountedTargetValues,
                                      Map<T2, Integer> chainedCountedTargetValues, int searchResultSize) {
            this.firstCountedTargetValues = firstCountedTargetValues;
            this.chainedCountedTargetValues = chainedCountedTargetValues;
            this.searchResultSize = searchResultSize;
        }
    }

    /**
     * Iterates through a list of elements, counts their target values and finds all elements whose target value matched
     * a given value.
     *
     * @param baseList The list to be iterated through
     * @param listElementToTargetValue Can retrieve the target value from each element in <i>baseList</i>
     * @param targetValueToBeSearched All elements in <i>baseList</i>, whose target value matches this value, will be
     *                                put in a list of search results. If this value is null, the search operation will
     *                                not take place.
     * @param onlySearching Whether to only do the search operation and not the counting operation.
     * @param <B> Type of the elements in <i>baseList</i>
     * @param <T> Type of the target values retrieved from each element in <i>baseList</i>
     * @return An object containing the search result list and a map with all target values as its keys and how often
     * that target value appeared as its corresponding value. See {@link CountAndSearchResult CountAndSearchResult} for
     * more details.
     */
    private static <B, T> CountAndSearchResult<B, T> countAndSearch(List<B> baseList,
                                                                    Function<B, T> listElementToTargetValue,
                                                                    T targetValueToBeSearched, boolean onlySearching) {
        Map<T, Integer> previewMap = null;
        if (!onlySearching) {
            previewMap = new HashMap<>();
        }
        List<B> searchResultList = null;
        if (targetValueToBeSearched != null) {
            searchResultList = new ArrayList<>();
        }

        for (B currentElement : baseList) {
            final T currentMappedValue = listElementToTargetValue.apply(currentElement);
            if (!onlySearching) {
                // Logging how often this target value occurred
                if (!previewMap.containsKey(currentMappedValue)) {
                    previewMap.put(currentMappedValue, 1);
                } else {
                    previewMap.put(currentMappedValue, previewMap.get(currentMappedValue) + 1);
                }
            }
            // Adds the baseList element to the search result list if conditions apply
            if (/*targetValueToBeSearched != null && */currentMappedValue.equals(targetValueToBeSearched)) {
                searchResultList.add(currentElement);
            }
        }

        return new CountAndSearchResult<>(previewMap, searchResultList);
    }

    /**
     * Performs a {@link #countAndSearch(List, Function, Object, boolean) countAndSearch}, processes its search results
     * and performs a second countAndSearch with the processed search results. (The second countAndSearch actually
     * doesn't search, it only counts)
     *
     * @param baseList The <i>baseList</i> for the first countAndSearch
     * @param baseListElementToTargetValue1 The <i>listElementToTargetValue</i> for the first countAndSearch
     * @param targetValue1ToBeSearched The <i>targetValueToBeSearched</i> for the first countAndSearch
     * @param processSearchResults Processes the list of search results into another list.
     * @param searchResultObjectToTargetValue2 The <i>listElementToTargetValue</i> for the second countAndSearch
     * @param disableFirstCounting Whether to prevent the counting operation during the first countAndSearch
     * @param <B> The type of the <i>baseList</i> elements in the first countAndSearch
     * @param <S> The type of elements in the list of processed search results
     * @param <T1> The type of the target value during the first countAndSearch
     * @param <T2> The type of the target value during the second countAndSearch
     * @return An object containing the counting results of both countAndSearch operations, see
     * {@link ChainedCountingResult} for more details.
     */
    private static <B, S, T1, T2> ChainedCountingResult<T1, T2> chainedCounting(List<B> baseList,
            Function<B, T1> baseListElementToTargetValue1, T1 targetValue1ToBeSearched,
            Function<List<B>, List<S>> processSearchResults, Function<S, T2> searchResultObjectToTargetValue2,
            boolean disableFirstCounting) {

        CountAndSearchResult<B, T1> countAndSearchResult = countAndSearch(baseList,
                baseListElementToTargetValue1, targetValue1ToBeSearched, disableFirstCounting);
        List<S> processedResults = processSearchResults.apply(countAndSearchResult.searchResult);
        Map<T2, Integer> chainedMap = countAndSearch(processedResults, searchResultObjectToTargetValue2,
                null, false).countedTargetValues;

        return new ChainedCountingResult<>(countAndSearchResult.countedTargetValues, chainedMap,
                countAndSearchResult.searchResult.size());
    }

    /**
     * Provides preview numbers specifically for a certain Responsibility filtering feature. See
     * {@link ResponsibilityFilterPreviewResponseDTO} for more information on what this method does.
     *
     * @param projectId          The id of the project the Responsibilities to be filtered are in.
     * @param systemConstraintId The id of the currently selected (and currently filtered) System Constraint. Must be
     *                           null if no filtering by System Constraint takes place at the moment.
     * @param controllerId       The id of the currently selected (and currently filtered) Controller. Must be null if
     *                           no filtering by Controller takes place at the moment.
     * @return The resulting preview numbers for the preview tables, see {@link ResponsibilityFilterPreviewResponseDTO}
     * for more details.
     */
    public ResponsibilityFilterPreviewResponseDTO getResponsibilityFilterPreview(UUID projectId,
                                                                                 Integer systemConstraintId,
                                                                                 Integer controllerId) {
        ResponsibilityFilterPreviewResponseDTO resultDto = new ResponsibilityFilterPreviewResponseDTO();
        List<Responsibility> responsibilities = responsibilityData.findAll(projectId);
        List<ResponsibilitySystemConstraintLink> links = respoScLinkDAO.getAllLinks(projectId, false);

        if (systemConstraintId == null && controllerId == null) {
            Map<Integer, Integer> controllerPreview = countAndSearch(responsibilities, Responsibility::getControllerId,
                    null, false).countedTargetValues;
            Map<Integer, Integer> sysConPreview = countAndSearch(links,
                    ResponsibilitySystemConstraintLink::getSystemConstraintId,
                    null, false).countedTargetValues;

            resultDto.setAllSystemConstraintsPreview(responsibilities.size());
            resultDto.setAllControllersPreview(responsibilities.size());
            resultDto.setSystemConstraintIdToPreviewMap(sysConPreview);
            resultDto.setControllerIdToPreviewMap(controllerPreview);


        } else if (systemConstraintId != null && controllerId == null) {
            ChainedCountingResult<Integer, Integer> chainedCountingResult = chainedCounting(links,
                    ResponsibilitySystemConstraintLink::getSystemConstraintId, systemConstraintId,
                    l -> linkListToResponsibilityList(projectId, l), Responsibility::getControllerId,
                    false);

            resultDto.setAllSystemConstraintsPreview(responsibilities.size());
            resultDto.setAllControllersPreview(chainedCountingResult.firstCountedTargetValues.getOrDefault(
                    systemConstraintId, 0));
            resultDto.setSystemConstraintIdToPreviewMap(chainedCountingResult.firstCountedTargetValues);
            resultDto.setControllerIdToPreviewMap(chainedCountingResult.chainedCountedTargetValues);


        } else if (systemConstraintId == null/* && controllerId != null*/) {
            ChainedCountingResult<Integer, Integer> chainedCountingResult = chainedCounting(responsibilities,
                    Responsibility::getControllerId, controllerId, r -> responsibilityListToLinkList(projectId, r),
                    ResponsibilitySystemConstraintLink::getSystemConstraintId, false);

            resultDto.setAllSystemConstraintsPreview(chainedCountingResult.firstCountedTargetValues.getOrDefault(
                    controllerId, 0));
            resultDto.setAllControllersPreview(responsibilities.size());
            resultDto.setSystemConstraintIdToPreviewMap(chainedCountingResult.chainedCountedTargetValues);
            resultDto.setControllerIdToPreviewMap(chainedCountingResult.firstCountedTargetValues);


        } else {
            ChainedCountingResult<Integer, Integer> controllerRequest = chainedCounting(links,
                    ResponsibilitySystemConstraintLink::getSystemConstraintId, systemConstraintId,
                    l -> linkListToResponsibilityList(projectId, l), Responsibility::getControllerId,
                    true
            );
            ChainedCountingResult<Integer, Integer> constraintRequest = chainedCounting(responsibilities,
                    Responsibility::getControllerId, controllerId, r -> responsibilityListToLinkList(projectId, r),
                    ResponsibilitySystemConstraintLink::getSystemConstraintId, true
            );

            resultDto.setAllSystemConstraintsPreview(constraintRequest.searchResultSize);
            resultDto.setAllControllersPreview(controllerRequest.searchResultSize);
            resultDto.setSystemConstraintIdToPreviewMap(constraintRequest.chainedCountedTargetValues);
            resultDto.setControllerIdToPreviewMap(controllerRequest.chainedCountedTargetValues);
        }
        return resultDto;
    }

    /**
     * Accepts a list of {@link ResponsibilitySystemConstraintLink}s, finds the {@link Responsibility}s they are linking
     * to and returns them in a distinct list.
     *
     * @param projectId The id of the project, the Links and Responsibilities are in.
     * @param links The list of Links.
     * @return The distinct list of Responsibilities.
     */
    private List<Responsibility> linkListToResponsibilityList(UUID projectId,
                                                              List<ResponsibilitySystemConstraintLink> links) {
        List<Integer> responsibilityIds = links.stream()
                .map(ResponsibilitySystemConstraintLink::getResponsibilityId)
                .distinct()
                .collect(Collectors.toList());
        return responsibilityData.getListByResponsibilityIds(projectId, responsibilityIds);
    }

    /**
     * Accepts a list of {@link Responsibility}s, finds all {@link ResponsibilitySystemConstraintLink}s that link to
     * them and returns them in a distinct list.
     *
     * @param projectId The id of the project, the Links and Responsibilities are in.
     * @param responsibilities The list of Responsibilities.
     * @return The distinct list of Links.
     */
    private List<ResponsibilitySystemConstraintLink> responsibilityListToLinkList(UUID projectId,
                List<Responsibility> responsibilities) {

        responsibilities = responsibilities.stream().distinct().collect(Collectors.toList());
        Set<ResponsibilitySystemConstraintLink> linkSet = new HashSet<>();
        for (Responsibility responsibility : responsibilities) {
            linkSet.addAll(respoScLinkDAO.getLinksByResponsibilityId(projectId, responsibility.getId().getId(),
                    Integer.MAX_VALUE, 0));
        }
        return new ArrayList<>(linkSet);
    }
}
