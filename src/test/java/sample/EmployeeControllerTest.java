package sample;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.datatables.mapping.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testWithGET() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("draw", "1");
        params.add("length", "2");
        params.add("columns[0].data", "id");
        params.add("columns[0].name", "id");
        params.add("columns[0].searchable", "true");
        params.add("columns[0].orderable", "true");
        params.add("columns[0].search.value", "");
        params.add("order[0].column", "0");
        params.add("order[0].dir", "asc");

        MvcResult result = this.mockMvc.perform(get("/employees").params(params))
                .andExpect(status().isOk())
                .andReturn();
        DataTablesOutput output = objectMapper.readValue(result.getResponse().getContentAsString(), DataTablesOutput.class);

        assertThat(output.getDraw()).isEqualTo(1);
        assertThat(output.getData().size()).isEqualTo(2);
        assertThat(output.getRecordsTotal()).isEqualTo(5000);
    }

    @Test
    void testWithGET_noParams() throws Exception {
        this.mockMvc.perform(get("/employees"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testWithPOST() throws Exception {
        DataTablesInput input = new DataTablesInput();
        input.setDraw(3);
        input.setLength(4);
        input.setColumns(singletonList(new Column("id", "id", true, true, new Search())));
        input.setOrder(singletonList(new Order(0, "asc")));

        MvcResult result = this.mockMvc.perform(post("/employees")
                    .content(objectMapper.writeValueAsString(input))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        DataTablesOutput output = objectMapper.readValue(result.getResponse().getContentAsString(), DataTablesOutput.class);

        assertThat(output.getDraw()).isEqualTo(3);
        assertThat(output.getData().size()).isEqualTo(4);
        assertThat(output.getRecordsTotal()).isEqualTo(5000);
    }
}