package com.naum.system.contract;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

public final class ContractSamples {

    private ContractSamples() {
    }

    public static String moneyCostsV2() {
        return read("contract/money-costs-v2.json");
    }

    private static String read(String path) {
        try (InputStream in = ContractSamples.class.getClassLoader().getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalStateException("Contract sample not found on classpath: " + path);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
