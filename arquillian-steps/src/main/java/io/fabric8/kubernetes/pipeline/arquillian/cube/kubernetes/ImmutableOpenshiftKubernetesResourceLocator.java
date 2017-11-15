package io.fabric8.kubernetes.pipeline.arquillian.cube.kubernetes;


import io.fabric8.kubernetes.clnt.v2_6.KubernetesClient;
import io.fabric8.openshift.clnt.v2_6.OpenShiftClient;
import org.arquillian.cube.kubernetes.api.Configuration;
import org.arquillian.cube.kubernetes.api.KubernetesResourceLocator;
import org.arquillian.cube.kubernetes.impl.locator.DefaultKubernetesResourceLocator;
import org.arquillian.cube.openshift.impl.client.CubeOpenShiftConfiguration;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ImmutableOpenshiftKubernetesResourceLocator extends DefaultKubernetesResourceLocator {

    private static final String[] RESOURCE_NAMES =
        new String[] {"openshift", "META-INF/fabric8/openshift", "kubernetes", "META-INF/fabric8/kubernetes"};

    private final KubernetesClient client;

    private final Configuration configuration;

    public ImmutableOpenshiftKubernetesResourceLocator(KubernetesClient client, Configuration configuration) {
        this.client = client;
        this.configuration = configuration;
    }

    @Override
    protected String[] getResourceNames() {
        if (!client.isAdaptable(OpenShiftClient.class)) {
            return super.getResourceNames();
        }
        return RESOURCE_NAMES;
    }

    @Override
    public Collection<URL> locateAdditionalResources() {
        if (!client.isAdaptable(OpenShiftClient.class)) {
            return super.locateAdditionalResources();
        }


        if (configuration instanceof CubeOpenShiftConfiguration && ((CubeOpenShiftConfiguration) configuration).isEnableImageStreamDetection()) {
            List<URL> additionalUrls = new LinkedList<>();
            File targetDir = new File(System.getProperty("basedir", ".") + "/target");
            if (targetDir.exists() && targetDir.isDirectory()) {
                File[] files = targetDir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.getName().endsWith("-is.yml")) {
                            try {
                                additionalUrls.add(file.toURI().toURL());
                            } catch (MalformedURLException e) {
                                // ignore
                            }
                        }
                    }
                }
            }
            return additionalUrls;
        }

        return Collections.emptyList();
    }

    @Override
    public KubernetesResourceLocator toImmutable() {
        return this;
    }
}
