package com.kobi.example.demo;


import com.kobi.example.demo.config.TestsConfig;
import com.kobi.example.demo.controller.DataController;
import com.kobi.example.demo.exception.DataNotFoundException;
import com.kobi.example.demo.service.DataNotifier;
import com.kobi.example.demo.service.DataService;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.concurrent.atomic.AtomicReference;

import static com.kobi.example.demo.utils.RestUtils.createBasicAuth;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@WebAppConfiguration
@SpringBootTest
@ActiveProfiles("mock")
@Import(TestsConfig.class)
@Slf4j
public class ApplicationIT {

    @Autowired
    TestsConfig testsConfig;

	@SpyBean
	private DataService dataService;

	@Autowired
    AtomicReference<JSONObject> atomicReference;

	@SpyBean
	private DataNotifier dataNotifier;

	@SpyBean
    private DataController dataController;

	@Autowired
	WebApplicationContext wac;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    private MockMvc mockMvc;
    private JSONObject jsonObject;

	@Before
    @SuppressWarnings("unchecked")
	public void init() {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .addFilter(springSecurityFilterChain)
				.build();

		jsonObject = new JSONObject();
        jsonObject.put("key", "value");
        jsonObject.put("1", 1);
	}

    private void verifyDataIsNotSet() {
       assertNull(atomicReference.get());
    }

	@Test
	public void getData_noDataWereAssigned_404() throws Exception {
	    verifyDataIsNotSet();

		MvcResult result = mockMvc.perform(get("/v1/data"))
				.andExpect(status().isNotFound())
                .andReturn();

		assertEquals(DataNotFoundException.class, result.getResolvedException().getClass());
		verify(dataService, times(1)).getLatestData();
		verifyNoMoreInteractions(dataService);
		verifyZeroInteractions(dataNotifier);
        verifyDataIsNotSet();
	}

    @Test
    public void getData_dataExists_dataIsReturned() throws Exception {
        atomicReference.set(jsonObject);

        mockMvc.perform(get("/v1/data")
                .content(MediaType.APPLICATION_JSON.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().string(jsonObject.toJSONString()));

        verify(dataService).getLatestData();
        verifyNoMoreInteractions(dataNotifier);
    }

    @Test
    public void createData_newDataWereAssigned_dataWasStoredAndNotifierNotified() throws Exception {
	    verifyDataIsNotSet();
	    when(testsConfig.getMockedRestTemplate()
                .exchange(eq(testsConfig.getNotifierUri()), eq(HttpMethod.PUT), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(post("/v1/data")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonObject.toJSONString()))
                .andExpect(status().isCreated());

        assertEquals(jsonObject, atomicReference.get());
        verify(dataService).create(jsonObject);
        verify(dataNotifier).onApplicationEvent(any());
	}

    @Test
    public void createData_notifierFiledToNotify_dataIsNotStored412() throws Exception {
        verifyDataIsNotSet();
        when(testsConfig.getMockedRestTemplate()
                .exchange(eq(testsConfig.getNotifierUri()), eq(HttpMethod.PUT), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

        mockMvc.perform(post("/v1/data")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonObject.toJSONString()))
                .andExpect(status().isPreconditionFailed());

        verifyDataIsNotSet();
        verify(dataService).create(jsonObject);
        verify(dataNotifier).onApplicationEvent(any());
    }

    @Test
    public void updateData_requestIsValid_dataWasUpdated() throws Exception {
        verifyDataIsNotSet();

        mockMvc.perform(put("/v1/update")
                .header(HttpHeaders.AUTHORIZATION, createBasicAuth("admin", "admin"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonObject.toJSONString()))
                .andExpect(status().isOk());

        assertEquals(jsonObject, atomicReference.get());
        verify(dataService).update(jsonObject);
        verifyZeroInteractions(dataNotifier);
    }
    @Test
    public void updateData_emptyCredentials_401() throws Exception {
        verifyDataIsNotSet();

        mockMvc.perform(put("/v1/update")
                .content(jsonObject.toJSONString()))
                .andExpect(status().isUnauthorized());

        verifyZeroInteractions(dataNotifier);
    }

    @Test
    public void updateData_wrongCredentials_401() throws Exception {
        verifyDataIsNotSet();

        mockMvc.perform(put("/v1/update")
                .header(HttpHeaders.AUTHORIZATION, createBasicAuth("wrong", "wrong"))
                .content(jsonObject.toJSONString()))
                .andExpect(status().isUnauthorized());

        verifyZeroInteractions(dataNotifier);
    }


}
