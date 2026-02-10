package com.example.demo.service.ai;

public interface EmbeddingProvider {
    float[] embed(String text);
}
