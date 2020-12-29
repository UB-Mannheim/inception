/*
 * Copyright 2012
 * Ubiquitous Knowledge Processing (UKP) Lab and FG Language Technology
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
package de.tudarmstadt.ukp.clarin.webanno.ui.curation.component.model;

import static org.apache.wicket.markup.head.JavaScriptHeaderItem.forReference;

import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.feedback.IFeedback;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.googlecode.wicket.jquery.ui.settings.JQueryUILibrarySettings;

import de.tudarmstadt.ukp.clarin.webanno.api.ProjectService;
import de.tudarmstadt.ukp.clarin.webanno.brat.annotation.BratVisualizer;
import de.tudarmstadt.ukp.clarin.webanno.brat.resource.BratAjaxResourceReference;
import de.tudarmstadt.ukp.clarin.webanno.brat.resource.BratAnnotatorUiResourceReference;
import de.tudarmstadt.ukp.clarin.webanno.brat.resource.BratConfigurationResourceReference;
import de.tudarmstadt.ukp.clarin.webanno.brat.resource.BratCurationUiResourceReference;
import de.tudarmstadt.ukp.clarin.webanno.brat.resource.BratDispatcherResourceReference;
import de.tudarmstadt.ukp.clarin.webanno.brat.resource.BratUtilResourceReference;
import de.tudarmstadt.ukp.clarin.webanno.brat.resource.BratVisualizerResourceReference;
import de.tudarmstadt.ukp.clarin.webanno.brat.resource.BratVisualizerUiResourceReference;
import de.tudarmstadt.ukp.clarin.webanno.brat.resource.JQueryJsonResourceReference;
import de.tudarmstadt.ukp.clarin.webanno.brat.resource.JQuerySvgDomResourceReference;
import de.tudarmstadt.ukp.clarin.webanno.brat.resource.JQuerySvgResourceReference;
import de.tudarmstadt.ukp.clarin.webanno.model.Mode;
import de.tudarmstadt.ukp.clarin.webanno.model.Project;
import de.tudarmstadt.ukp.clarin.webanno.security.UserDao;

/**
 * Wicket panel for visualizing an annotated sentence in brat. When a user clicks on a span or an
 * arc, the Method onSelectAnnotationForMerge() is called. Override that method to receive the
 * result in another Wicket panel.
 */
public abstract class BratSuggestionVisualizer
    extends BratVisualizer
{
    private static final long serialVersionUID = 6653508018500736430L;

    private AbstractDefaultAjaxBehavior controller;

    private @SpringBean ProjectService projectService;
    private @SpringBean UserDao userService;

    public BratSuggestionVisualizer(String id, IModel<UserAnnotationSegment> aModel, int aPosition)
    {
        super(id, aModel);
        String username;
        if (getModelObject().getAnnotatorState().getMode().equals(Mode.AUTOMATION)
                || getModelObject().getAnnotatorState().getMode().equals(Mode.CORRECTION)) {
            username = "Suggestion";
        }
        else {
            Project project = getModelObject().getAnnotatorState().getProject();
            if (project.isAnonymousCuration()
                    && !projectService.isManager(project, userService.getCurrentUser())) {
                username = "Anonymized annotator " + (aPosition + 1);
            }
            else {
                username = getModelObject().getUsername();
            }
        }

        add(new Label("username", username));

        controller = new AbstractDefaultAjaxBehavior()
        {
            private static final long serialVersionUID = 1133593826878553307L;

            @Override
            protected void respond(AjaxRequestTarget aTarget)
            {
                try {
                    onClientEvent(aTarget);
                }
                catch (Exception e) {
                    aTarget.addChildren(getPage(), IFeedback.class);
                    error("Error: " + e.getMessage());
                }
            }

        };
        add(controller);
    }

    public void setModel(IModel<UserAnnotationSegment> aModel)
    {
        setDefaultModel(aModel);
    }

    public void setModelObject(UserAnnotationSegment aModel)
    {
        setDefaultModelObject(aModel);
    }

    @SuppressWarnings("unchecked")
    public IModel<UserAnnotationSegment> getModel()
    {
        return (IModel<UserAnnotationSegment>) getDefaultModel();
    }

    public UserAnnotationSegment getModelObject()
    {
        return (UserAnnotationSegment) getDefaultModelObject();
    }

    @Override
    public void renderHead(IHeaderResponse aResponse)
    {
        // MUST NOT CALL super.renderHead here because that would call Util.embedByUrl again!
        // super.renderHead(aResponse);

        // Libraries
        aResponse.render(forReference(JQueryUILibrarySettings.get().getJavaScriptReference()));
        aResponse.render(JavaScriptHeaderItem.forReference(JQuerySvgResourceReference.get()));
        aResponse.render(JavaScriptHeaderItem.forReference(JQuerySvgDomResourceReference.get()));
        aResponse.render(JavaScriptHeaderItem.forReference(JQueryJsonResourceReference.get()));

        // BRAT helpers
        aResponse.render(
                JavaScriptHeaderItem.forReference(BratConfigurationResourceReference.get()));
        aResponse.render(JavaScriptHeaderItem.forReference(BratUtilResourceReference.get()));
        // aResponse.render(JavaScriptHeaderItem.forReference(
        // BratAnnotationLogResourceReference.get()));
        // aResponse.render(JavaScriptHeaderItem.forReference(BratSpinnerResourceReference.get()));

        // BRAT modules
        aResponse.render(JavaScriptHeaderItem.forReference(BratDispatcherResourceReference.get()));
        aResponse.render(JavaScriptHeaderItem.forReference(BratAjaxResourceReference.get()));
        aResponse.render(JavaScriptHeaderItem.forReference(BratVisualizerResourceReference.get()));
        aResponse
                .render(JavaScriptHeaderItem.forReference(BratVisualizerUiResourceReference.get()));
        aResponse.render(JavaScriptHeaderItem.forReference(BratAnnotatorUiResourceReference.get()));
        aResponse.render(JavaScriptHeaderItem.forReference(BratCurationUiResourceReference.get()));
        // aResponse.render(
        // JavaScriptHeaderItem.forReference(BratUrlMonitorResourceReference.get()));

        // BRAT call to load the BRAT JSON from our collProvider and docProvider.
        // @formatter:off
        String script = 
                "Util.embedByURL(" 
                + "  '" + vis.getMarkupId() + "'," 
                + "  '" + collProvider.getCallbackUrl() + "', " 
                + "  '" + docProvider.getCallbackUrl() + "', " 
                + "  function(dispatcher) {" 
                + "    dispatcher.wicketId = '" + vis.getMarkupId() + "'; " 
                + "    dispatcher.ajaxUrl = '" + controller.getCallbackUrl() + "'; " 
                + "    var ajax = new Ajax(dispatcher);"
                + "    var curation_mod = new CurationMod(dispatcher, '" + vis.getMarkupId() + "');"
                + "    Wicket.$('" + vis.getMarkupId() + "').dispatcher = dispatcher;"
                // + " dispatcher.post('clearSVG', []);"
                + "  });";
        // @formatter:on
        aResponse.render(OnLoadHeaderItem.forScript("\n" + script));
    }

    @Override
    public String getDocumentData()
    {
        return getModelObject().getDocumentResponse() == null ? "{}"
                : getModelObject().getDocumentResponse();
    }

    @Override
    protected String getCollectionData()
    {
        return getModelObject().getCollectionData();
    }

    protected abstract void onClientEvent(AjaxRequestTarget aTarget) throws Exception;
}
