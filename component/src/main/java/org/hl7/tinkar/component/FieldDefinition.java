/*
 * Copyright 2020-2021 HL7.
 *
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
 */package org.hl7.tinkar.component;


import org.eclipse.collections.api.list.ImmutableList;

import java.util.UUID;

/**
 *
 * @author kec
 */
public interface FieldDefinition {

    /**
     * Underlying object type such as String or Integer.
     * @return Concept designating the data type of the defined field.
     */
    Concept getDataType();

    /**
     * What the object represents: a String might be a URI,
     * a component identifier might represent a mapping, or an
     * integer might represent a coordinate.
     * @return Concept designating the purpose of the defined field.
     */
    Concept getPurpose();

    /**
     * The identity of this field. Maybe it is the
     * "SNOMED code" in a mapping, or the location of an image if a URI.
     * This concept should be used to present to teh user what this field "is" in
     * interfaces and similar.
     * <br/>
     * Other names to consider: what, as in "what is this" which is slightly different/weaker that identity which
     * some might tie in with instance value.
     * @return Concept designating the identity defined field.
     */
    Concept getIdentity();

}
