package com.sb.woohoo.data.network;

import com.sb.woohoo.utils.Types;

import java.lang.reflect.Type;

import javax.annotation.Nullable;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

//TODO don't remove will be used later
public class EnvelopeConverterFactory extends Converter.Factory {

    @Nullable
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(
            Type type,
            java.lang.annotation.Annotation[] annotations,
            Retrofit retrofit) {

        Type envelopeType = Types.newParameterizedType(Envelope.class, type);

        Converter<ResponseBody, Envelope> delegate =
                retrofit.nextResponseBodyConverter(this, envelopeType, annotations);
        return new EnvelopeConverter(delegate);
    }
}
