/*-
 * ---license-start
 * eu-digital-green-certificates / dgca-businessrule-service
 * ---
 * Copyright (C) 2021 T-Systems International GmbH and all other contributors
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ---license-end
 */

package eu.europa.ec.dgc.businessrule.service;

import eu.europa.ec.dgc.businessrule.entity.BusinessRuleEntity;
import eu.europa.ec.dgc.businessrule.repository.BusinessRuleRepository;
import eu.europa.ec.dgc.businessrule.restapi.dto.BusinessRuleListItemDto;
import eu.europa.ec.dgc.businessrule.utils.BusinessRulesUtils;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@RequiredArgsConstructor
@Service
public class BusinessRuleService {

    private final BusinessRulesUtils businessRulesUtils;

    private final BusinessRuleRepository businessRuleRepository;

    /**
     *  Saves a Business rule.
     *
     */
    @Transactional
    public void saveBusinessRule(String ruleId, String ruleCountry, String ruleData) {
        String hash;
        try {
            hash = businessRulesUtils.calculateHash(ruleData);
        } catch (NoSuchAlgorithmException e) {
            log.error("Calculation of hash failed:", e);
            return;
        }

        BusinessRuleEntity bre = new BusinessRuleEntity();
        bre.setIdentifier(ruleId);
        bre.setCountry(ruleCountry);
        bre.setRawData(ruleData.toUpperCase(Locale.ROOT));
        bre.setHash(hash);

        businessRuleRepository.save(bre);
    }

    /**
     *  Gets list of all business rules ids and hashes.
     */
    public List<BusinessRuleListItemDto> getBusinessRulesList() {

        List<BusinessRuleListItemDto> rulesItems = businessRuleRepository.findAllByOrderByIdentifierAsc();
        return rulesItems;
    }

    /**
     *  Gets list of all business rules ids and hashes for a country.
     */
    public List<BusinessRuleListItemDto> getBusinessRulesListForCountry(String country) {

        List<BusinessRuleListItemDto> rulesItems =
            businessRuleRepository.findAllByCountryOrderByIdentifierAsc(country.toUpperCase(Locale.ROOT));
        return rulesItems;
    }


    /**f
     *  Gets  a business rule by hash.
     */
    @Transactional
    public BusinessRuleEntity getBusinessRuleByCountryAndHash(String country, String hash) {

        return  businessRuleRepository.findOneByCountryAndHash(country, hash);
    }
}
