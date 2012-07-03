package org._10ne.jenkins;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import hudson.util.FormValidation;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;

/**
 * @author Noam Y. Tenne
 */
public class MotivationNotifier extends Notifier {

    private boolean motivateFailures;

    public boolean isMotivateFailures() {
        return motivateFailures;
    }

    @DataBoundConstructor
    public MotivationNotifier(boolean motivateFailures) {
        this.motivateFailures = motivateFailures;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        if (Result.FAILURE.equals(build.getResult()) && motivateFailures) {
            String motivatingMessage = getDescriptor().getDefaultMotivatingMessage();

            listener.getLogger().println("######" + motivatingMessage + "######");
        }
        return true;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return ((DescriptorImpl) super.getDescriptor());
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        private String defaultMotivatingMessage = "The build failed! Nice going, buddy!";

        public FormValidation doCheckDefaultMotivatingMessage(@QueryParameter String value) {
            if (StringUtils.isNotBlank(value)) {
                return FormValidation.ok();
            } else {
                return FormValidation.error("Y U NO INSULT?!");
            }
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject form) throws FormException {
            defaultMotivatingMessage = form.getString("defaultMotivatingMessage");
            save();
            return super.configure(req, form);
        }

        @Override
        public String getDisplayName() {
            return "Motivation Plugin";
        }

        public String getDefaultMotivatingMessage() {
            return defaultMotivatingMessage;
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }
    }
}