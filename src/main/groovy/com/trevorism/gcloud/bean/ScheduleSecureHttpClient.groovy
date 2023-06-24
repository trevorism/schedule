package com.trevorism.gcloud.bean

import com.trevorism.bean.PassThruSecureHttpClient
import com.trevorism.https.InternalTokenSecureHttpClient
import com.trevorism.https.SecureHttpClient
import io.micronaut.context.annotation.Replaces

@Replaces(value = PassThruSecureHttpClient)
@jakarta.inject.Singleton
class ScheduleSecureHttpClient extends InternalTokenSecureHttpClient implements SecureHttpClient {
}
