package com.github.tomakehurst.wiremock.verification;

import com.github.tomakehurst.wiremock.common.Json;
import com.github.tomakehurst.wiremock.matching.MatchResult;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.github.tomakehurst.wiremock.verification.NearMiss;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.http.RequestMethod.GET;
import static com.github.tomakehurst.wiremock.http.RequestMethod.HEAD;
import static com.github.tomakehurst.wiremock.matching.MockRequest.mockRequest;
import static com.github.tomakehurst.wiremock.testsupport.WireMatchers.equalToJson;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.skyscreamer.jsonassert.JSONCompareMode.LENIENT;

public class NearMissTest {

    static final String STUB_MAPPING_EXAMPLE =
        "{\n" +
        "  \"request\" : {\n" +
        "    \"url\" : \"/nearly-missed-me\",\n" +
        "    \"method\" : \"HEAD\"\n" +
        "  },\n" +
        "  \"stubMapping\": {\n" +
        "    \"request\": {\n" +
        "      \"url\" : \"/missed-me\",\n" +
        "      \"method\" : \"GET\"  \n" +
        "    },\n" +
        "    \"response\": {\n" +
        "      \"status\": 200\n" +
        "    }\n" +
        "  },\n" +
        "  \"matchResult\": {\n" +
        "    \"distance\": 0.5\n" +
        "  }\n" +
        "}";

    static final String REQUEST_PATTERN_EXAMPLE =
        "{\n" +
        "  \"request\" : {\n" +
        "    \"url\" : \"/nearly-missed-me\",\n" +
        "    \"method\" : \"HEAD\"\n" +
        "  },\n" +
        "  \"requestPattern\": {\n" +
        "    \"url\" : \"/missed-me\",\n" +
        "    \"method\" : \"GET\"\n" +
        "  },\n" +
        "  \"matchResult\": {\n" +
        "    \"distance\": 0.5\n" +
        "  }\n" +
        "}";

    @Test
    public void correctlySerialisesToJsonWithStubMapping() {
        String json = Json.write(new NearMiss(
            LoggedRequest.createFrom(mockRequest().method(HEAD).url("/nearly-missed-me")),
            get(urlEqualTo("/missed-me")).willReturn(aResponse()).build(),
            MatchResult.partialMatch(0.5)
        ));

        assertThat(json, equalToJson(STUB_MAPPING_EXAMPLE, LENIENT));
    }

    @Test
    public void correctlySerialisesToJsonWithRequestPattern() {
        String json = Json.write(new NearMiss(
            LoggedRequest.createFrom(mockRequest().method(HEAD).url("/nearly-missed-me")),
            get(urlEqualTo("/missed-me")).willReturn(aResponse()).build().getRequest(),
            MatchResult.partialMatch(0.5)
        ));

        assertThat(json, equalToJson(REQUEST_PATTERN_EXAMPLE, LENIENT));
    }

    @Test
    public void correctlyDeserialisesFromJsonWithStubMapping() {
        NearMiss nearMiss = Json.read(STUB_MAPPING_EXAMPLE, NearMiss.class);

        assertThat(nearMiss.getRequest().getUrl(), is("/nearly-missed-me"));
        assertThat(nearMiss.getRequest().getMethod(), is(HEAD));
        assertThat(nearMiss.getStubMapping().getRequest().getUrl(), is("/missed-me"));
        assertThat(nearMiss.getStubMapping().getRequest().getMethod(), is(GET));
        assertThat(nearMiss.getMatchResult().getDistance(), is(0.5));
        assertThat(nearMiss.getRequestPattern(), nullValue());
    }

    @Test
    public void correctlyDeserialisesFromJsonWithRequestPattern() {
        NearMiss nearMiss = Json.read(REQUEST_PATTERN_EXAMPLE, NearMiss.class);

        assertThat(nearMiss.getRequest().getUrl(), is("/nearly-missed-me"));
        assertThat(nearMiss.getRequest().getMethod(), is(HEAD));
        assertThat(nearMiss.getRequestPattern().getUrl(), is("/missed-me"));
        assertThat(nearMiss.getRequestPattern().getMethod(), is(GET));
        assertThat(nearMiss.getMatchResult().getDistance(), is(0.5));
        assertThat(nearMiss.getStubMapping(), nullValue());
    }
}