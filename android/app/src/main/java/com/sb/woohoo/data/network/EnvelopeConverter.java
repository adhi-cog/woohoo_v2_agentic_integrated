package com.sb.woohoo.data.network;

import java.io.IOException;

import androidx.annotation.NonNull;
import okhttp3.ResponseBody;
import retrofit2.Converter;

//TODO don't remove will be used later
class EnvelopeConverter<T> implements Converter<ResponseBody, T> {
    private final Converter<ResponseBody, Envelope<T>> delegate;

    EnvelopeConverter(Converter<ResponseBody, Envelope<T>> delegate) {
        this.delegate = delegate;
    }

    @Override
    public T convert(@NonNull ResponseBody responseBody) throws IOException {
        Envelope<T> envelope = delegate.convert(responseBody);
        return envelope.data;
    }
}
