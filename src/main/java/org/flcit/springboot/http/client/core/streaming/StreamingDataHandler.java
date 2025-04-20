/*
 * Copyright 2002-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.flcit.springboot.http.client.core.streaming;

import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Consumer;

import javax.activation.DataHandler;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
public class StreamingDataHandler extends DataHandler {

    private final Consumer<OutputStream> consumer;

    /**
     * @param consumer
     * @param dataSource
     */
    public StreamingDataHandler(Consumer<OutputStream> consumer, StreamingDataSource dataSource) {
        super(dataSource);
        this.consumer = consumer;
    }

    /**
     * @param consumer
     * @param contentType
     * @param name
     */
    public StreamingDataHandler(Consumer<OutputStream> consumer, String contentType, String name) {
        this(consumer, new StreamingDataSource(contentType, name));
    }

    @Override
    public void writeTo(OutputStream os) throws IOException {
        consumer.accept(os);
    }

}
