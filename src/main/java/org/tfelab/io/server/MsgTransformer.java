package org.tfelab.io.server;


import org.tfelab.json.JSONable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import spark.ResponseTransformer;

/**
 * 
 * @author: karajan@tfelab.org
 * @Date: 2016-03-31
 */
public class MsgTransformer implements ResponseTransformer {
			
	private ObjectMapper mapper = new ObjectMapper();

	@Override
	public String render(Object model) throws JsonProcessingException {
		
		if(model instanceof JSONable){
			return ((JSONable) model).toJSON();
		}
		else if (model instanceof ObjectNode) {
			return ((ObjectNode) model).toString();
		}
		else {
			return mapper.writeValueAsString(model);
		}
	}
}
