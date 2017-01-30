package org.springframework.cloud.servicebroker.model;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.cloud.servicebroker.model.fixture.ServiceInstanceBindingFixture;

public class CreateServiceInstanceVolumeBindingResponseTest {

    @Test
    public void canEqual() throws Exception {
        CreateServiceInstanceVolumeBindingResponse actual = ServiceInstanceBindingFixture.buildCreateBindingResponseForVolume();
        CreateServiceInstanceVolumeBindingResponse expected = ServiceInstanceBindingFixture.buildCreateBindingResponseForVolume();

        Assert.assertEquals(expected, actual);
    }

}