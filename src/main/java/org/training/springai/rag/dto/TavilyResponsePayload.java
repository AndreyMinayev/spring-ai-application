package org.training.springai.rag.dto;

import java.util.List;

public record TavilyResponsePayload(List<Hit> results) {
}
