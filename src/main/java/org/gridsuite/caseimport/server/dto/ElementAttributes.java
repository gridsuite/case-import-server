/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.gridsuite.caseimport.server.dto;

import lombok.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Abdelsalem HEDHILI <abdelsalem.hedhili at rte-france.com>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ElementAttributes {
    private UUID elementUuid;

    private String elementName;

    private String type;

    private AccessRightsAttributes accessRights;

    private String owner;

    private Long subdirectoriesCount;

    private String description;

    private Map<String, Object> specificMetadata = new HashMap<>();

    public ElementAttributes(UUID elementUuid, String elementName, String type, AccessRightsAttributes accessRights, String owner, long subdirectoriesCount, String description) {
        this(elementUuid, elementName, type, accessRights, owner, subdirectoriesCount, description, null);
    }
}
