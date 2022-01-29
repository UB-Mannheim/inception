/*
 * Licensed to the Technische Universität Darmstadt under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The Technische Universität Darmstadt 
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.
 *  
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.tudarmstadt.ukp.inception.recommendation.imls.elg.service;

import java.io.IOException;
import java.util.Optional;

import org.apache.uima.cas.CAS;

import de.tudarmstadt.ukp.clarin.webanno.model.Project;
import de.tudarmstadt.ukp.inception.recommendation.imls.elg.model.ElgServiceResponse;
import de.tudarmstadt.ukp.inception.recommendation.imls.elg.model.ElgSession;

public interface ElgService
{
    ElgSession signIn(Project aProject, String aSuccessCode) throws IOException;

    void signOut(Project aProject);

    Optional<ElgSession> getSession(Project aProject);

    void refreshSession(ElgSession aSession) throws IOException;

    ElgServiceResponse invokeService(ElgSession aSession, String aServiceSync, CAS aCas)
        throws IOException;

    ElgSession createOrUpdateSession(ElgSession aSession);
}