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

import eu.europa.ec.dgc.businessrule.entity.ValueSetEntity;
import eu.europa.ec.dgc.businessrule.repository.ValueSetRepository;
import eu.europa.ec.dgc.businessrule.restapi.dto.ValueSetListItemDto;
import eu.europa.ec.dgc.businessrule.utils.BusinessRulesUtils;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ValueSetService {

    private final BusinessRulesUtils businessRulesUtils;

    private final ValueSetRepository valueSetRepository;

    /**
     *  Saves a valueset.
     *
     */
    @Transactional
    public void saveValueSet(String valueSetName, String valueSetData) {
        String hash;
        try {
            hash = businessRulesUtils.calculateHash(valueSetData);
        } catch (NoSuchAlgorithmException e) {
            log.error("Calculation of hash failed:", e);
            return;
        }

        ValueSetEntity vse = new ValueSetEntity();
        vse.setId(valueSetName);
        vse.setRawData(valueSetData);
        vse.setHash(hash);

        valueSetRepository.save(vse);
    }

    /**
     *  gets list of all valueset ids and hashes.
     */
    public List<ValueSetListItemDto> getValueSetsList() {

        List<ValueSetListItemDto> valueSetItems = valueSetRepository.findAllByOrderByIdAsc();
        return valueSetItems;
    }


    /**
     *  Gets  valueset by hash.
     */
    @Transactional
    public ValueSetEntity getValueSetByHash(String hash) {

        return  valueSetRepository.findOneByHash(hash);
    }

}
