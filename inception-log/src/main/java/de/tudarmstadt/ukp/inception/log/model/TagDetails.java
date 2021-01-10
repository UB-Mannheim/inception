/*
 * Copyright 2021
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.tudarmstadt.ukp.inception.log.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import de.tudarmstadt.ukp.clarin.webanno.model.Tag;

@JsonInclude(Include.NON_NULL)
public class TagDetails
{
    private String tag;
    private String tagSet;

    public TagDetails()
    {
        // Nothing to do
    }

    public TagDetails(Tag aTag)
    {
        tag = aTag.getName();
        tagSet = aTag.getTagSet().getName();
    }

    public String getTag()
    {
        return tag;
    }

    public void setTag(String aTag)
    {
        tag = aTag;
    }

    public String getTagSet()
    {
        return tagSet;
    }

    public void setTagSet(String aTagSet)
    {
        tagSet = aTagSet;
    }
}