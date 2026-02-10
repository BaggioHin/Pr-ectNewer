package com.example.demo.service.ai;

import org.springframework.stereotype.Service;

@Service
public class DefaultEmbeddingProvider implements EmbeddingProvider {
    @Override
    public float[] embed(String text) {
        throw new UnsupportedOperationException(
                "No embedding provider configured. Replace DefaultEmbeddingProvider with a real implementation."
        );
    }
}
