package com.devsuperior.dscatalog.resources;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductResourceIT {

	@Autowired
	private MockMvc mockMvc;

	private long existingId;
	private long nonExistingId;
	private Long countTotalProducts;

	@Autowired
	private ObjectMapper objMapper;
	
	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		countTotalProducts = 25L;
	}

	@Test
	public void findAllShouldReturnSortedPagedWhenSortByName() throws Exception {
		ResultActions result = mockMvc
				.perform(get("/products?page=0&size=12&sort=name,asc").accept(MediaType.APPLICATION_JSON));
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.totalElements").value(countTotalProducts));
		result.andExpect(jsonPath("$.content").exists());
		result.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));

	}

	@Test
	public void updateShouldReturnProductDTOWhenIdExists() throws Exception {

		ProductDTO productDTO = Factory.createProductDTO();
		String jsonBody = objMapper.writeValueAsString(productDTO);
		
		String expectedName = productDTO.getName();
				
		ResultActions result = mockMvc.perform(put("/products/{id}", existingId).content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").value(existingId));
		result.andExpect(jsonPath("$.name").value(expectedName));
	}
	
	@Test
	public void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {

		ProductDTO productDTO = Factory.createProductDTO();
		String jsonBody = objMapper.writeValueAsString(productDTO);
		
				
		ResultActions result = mockMvc.perform(put("/products/{id}", nonExistingId).content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));

		result.andExpect(status().isNotFound());
	}
}
