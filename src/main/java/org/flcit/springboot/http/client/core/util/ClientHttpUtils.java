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

package org.flcit.springboot.http.client.core.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Supplier;
import java.util.zip.DeflaterInputStream;
import java.util.zip.GZIPInputStream;

import javax.servlet.http.HttpServletResponse;

import org.flcit.commons.core.file.util.FileUtils;
import org.flcit.commons.core.util.ReflectionUtils;
import org.flcit.springboot.commons.core.exception.EntityNotFoundException;
import org.flcit.springboot.commons.core.exception.NoContentException;
import org.flcit.springboot.commons.core.file.resource.FileInputStreamResource;
import org.flcit.springboot.commons.core.file.util.MediaTypeUtils;
import org.flcit.springboot.http.client.core.domain.QueryParamName;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

/**
 * 
 * @since 
 * @author Florian Lestic
 */
public final class ClientHttpUtils {

    private ClientHttpUtils() { }

    /**
     * @param resource
     * @param contentType
     * @return
     */
    public static HttpEntity<Resource> buildEntity(final Resource resource, final String contentType) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        return new HttpEntity<>(resource, headers);
    }

    private static MediaType getContentType(RestClientResponseException ex) {
        return getContentType(ex.getResponseHeaders());
    }

    private static MediaType getContentType(HttpHeaders headers) {
        return headers != null ? headers.getContentType() : null;
    }

    private static String getFirst(HttpHeaders headers, String headerName) {
        return headers != null ? headers.getFirst(headerName) : null;
    }

    /**
     * @param e
     * @return
     */
    public static ResponseEntity<Object> buildResponse(RestClientException e) {
        if (e instanceof RestClientResponseException) {
            RestClientResponseException ex = (RestClientResponseException) e;
            try {
                BodyBuilder builder = ResponseEntity.status(ex.getRawStatusCode());
                MediaType contentType = getContentType(ex);
                if (contentType != null) {
                    builder.contentType(contentType);
                }
                return builder.body(inputStream(ex));
            } catch (IOException e1) { /* DO NOTHING */ }
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * @param e
     * @param response
     */
    public static void buildResponse(RestClientException e, HttpServletResponse response) {
        if (e instanceof RestClientResponseException) {
            RestClientResponseException ex = (RestClientResponseException) e;
            try {
                response.setStatus(ex.getRawStatusCode());
                MediaType contentType = getContentType(ex);
                if (contentType != null) {
                    response.setContentType(contentType.toString());
                }
                StreamUtils.copy(inputStream(ex), response.getOutputStream());
            } catch (IOException e1) { /* DO NOTHING */ }
        } else {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    /**
     * @param clientReponse
     * @param response
     * @param headers
     * @throws IOException
     */
    public static void buildResponse(ClientHttpResponse clientReponse, HttpServletResponse response, String... headers) throws IOException {
        response.setStatus(clientReponse.getRawStatusCode());
        MediaType contentType = clientReponse.getHeaders().getContentType();
        if (contentType != null) {
            response.setContentType(contentType.toString());
        }
        if (headers != null) {
            for (String header: headers) {
                String value = clientReponse.getHeaders().getFirst(header);
                if (value != null) {
                    response.setHeader(header, value);
                }
            }
        }
        buildResponse(clientReponse, response.getOutputStream());
    }

    private static void buildResponse(ClientHttpResponse clientReponse, OutputStream outputStream) throws IOException {
        StreamUtils.copy(inputStream(clientReponse), outputStream);
    }

    private static InputStream inputStream(RestClientResponseException ex) throws IOException {
        return inputStream(ex.getResponseBodyAsByteArray(), getFirst(ex.getResponseHeaders(), HttpHeaders.CONTENT_ENCODING));
    }

    private static InputStream inputStream(ClientHttpResponse clientReponse) throws IOException {
        return inputStream(clientReponse.getBody(), getFirst(clientReponse.getHeaders(), HttpHeaders.CONTENT_ENCODING));
    }

    private static InputStream inputStream(byte[] bytes, String contentEncoding) throws IOException {
        return inputStream(new ByteArrayInputStream(bytes), contentEncoding);
    }

    private static InputStream inputStream(InputStream is, String contentEncoding) throws IOException {
        if ("gzip".equals(contentEncoding)) {
            return new GZIPInputStream(is);
        }
        return "deflate".equals(contentEncoding) ? new DeflaterInputStream(is) : is;
    }

    /**
     * @param clientHttpResponse
     * @return
     * @throws IOException
     */
    public static FileInputStreamResource convertToFileResource(ClientHttpResponse clientHttpResponse) throws IOException {
        return convertToFileResource(clientHttpResponse, null);
    }

    /**
     * @param clientHttpResponse
     * @param limitFilename
     * @return
     * @throws IOException
     */
    public static FileInputStreamResource convertToFileResource(ClientHttpResponse clientHttpResponse, Integer limitFilename) throws IOException {
        return convertToFileResource(clientHttpResponse, null, limitFilename, null);
    }

    private static String getFilename(ClientHttpResponse clientHttpResponse) {
        ContentDisposition contentDisposition = clientHttpResponse.getHeaders().getContentDisposition();
        String filename = contentDisposition.getFilename();
        if (filename != null && contentDisposition.getCharset() == null) {
            filename = new String(filename.getBytes(StandardCharsets.ISO_8859_1));
        }
        return filename;
    }

    /**
     * @param clientHttpResponse
     * @param filenameIfNull
     * @param limitFilename
     * @param contentTypeIfNull
     * @return
     * @throws IOException
     */
    public static FileInputStreamResource convertToFileResource(ClientHttpResponse clientHttpResponse, String filenameIfNull, Integer limitFilename, String contentTypeIfNull) throws IOException {
        String filename = getFilename(clientHttpResponse);
        if (filenameIfNull != null && !StringUtils.hasLength(filename)) {
            filename = filenameIfNull;
        }
        MediaType contentType = getContentType(clientHttpResponse.getHeaders());
        if (contentTypeIfNull != null && (contentType == null || contentType.equalsTypeAndSubtype(MediaType.APPLICATION_OCTET_STREAM))) {
            contentType = MediaType.parseMediaType(contentTypeIfNull);
        }
        if (contentType == null || contentType.equalsTypeAndSubtype(MediaType.APPLICATION_OCTET_STREAM)) {
            contentType = MediaTypeUtils.getByFilename(filename);
        }
        if (limitFilename != null) {
            filename = FileUtils.limitLength(filename, limitFilename);
        }
        return new FileInputStreamResource(clientHttpResponse.getBody(), filename, contentType, clientHttpResponse.getHeaders().getContentLength());
    }

    /**
     * @param runnable
     */
    public static void catchNotFound(Runnable runnable) {
        catchNotFound(runnable, null, null);
    }

    /**
     * @param <T>
     * @param supplier
     * @return
     */
    public static <T> T catchNotFound(Supplier<T> supplier) {
        return catchNotFound(supplier, null, null);
    }

    /**
     * @param runnable
     * @param table
     * @param id
     */
    public static void catchNotFound(Runnable runnable, String table, Object id) {
        try {
            runnable.run();
        } catch (HttpClientErrorException.NotFound e) {
            throw table != null && id != null ? new EntityNotFoundException(table, id) : new EntityNotFoundException(e);
        }
    }

    /**
     * @param <T>
     * @param supplier
     * @param table
     * @param id
     * @return
     */
    public static <T> T catchNotFound(Supplier<T> supplier, String table, Object id) {
        try {
            return supplier.get();
        } catch (HttpClientErrorException.NotFound e) {
            throw table != null && id != null ? new EntityNotFoundException(table, id) : new EntityNotFoundException(e);
        }
    }

    /**
     * @param <T>
     * @param supplier
     * @return
     */
    public static <T> T catchNoContent(Supplier<T> supplier) {
        return catchNoContent(supplier, null);
    }

    /**
     * @param <T>
     * @param supplier
     * @param message
     * @return
     */
    public static <T> T catchNoContent(Supplier<T> supplier, String message) {
        final T res = supplier.get();
        if (res == null) {
            throw new NoContentException(message);
        }
        return res;
    }

    /**
     * @param object
     * @return
     */
    public static Map<String, Object> toQueryParams(Object object) {
        return ReflectionUtils.toMap(object, false, false, QueryParamName.class, "value");
    }

}
