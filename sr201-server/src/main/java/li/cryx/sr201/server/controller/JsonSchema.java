package li.cryx.sr201.server.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/schema")
public class JsonSchema {

	@RequestMapping(value = "answer.json", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public Resource getAnswerSchema() {
		return new ClassPathResource("GenericAnswer.json");
	}

}
