package cloud.dreamcare.buncord.manager

import cloud.dreamcare.buncord.dsl.Listeners
import cloud.dreamcare.buncord.internal.services.InjectionService
import club.minnced.jda.reactor.ReactiveEventManager
import net.dv8tion.jda.api.hooks.SubscribeEvent
import org.springframework.beans.factory.config.AutowireCapableBeanFactory
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.classreading.MetadataReader
import org.springframework.core.type.classreading.MetadataReaderFactory
import org.springframework.stereotype.Component

@Component
class ReactiveEventManagerFactory(val beanFactory: AutowireCapableBeanFactory) {
    fun create(basePackage: String): ReactiveEventManager {
        val reactiveEventManager = ReactiveEventManager()
        val diService = InjectionService()

        // get all classes which have at least ONE SubscribeEvent annotation
        val scanner = ClassPathScanningCandidateComponentProvider(false)
        scanner.addIncludeFilter { metadataReader: MetadataReader, _: MetadataReaderFactory? ->
            metadataReader.annotationMetadata.getAnnotatedMethods(SubscribeEvent::class.java.name).isNotEmpty()
        }

        scanner.findCandidateComponents(basePackage).stream()
            .map { beanFactory.createBean(Class.forName(it.beanClassName)) } // Instantiate the class
            .forEach { listenerClass ->
                listenerClass.javaClass.methods.filter { it.isAnnotationPresent(SubscribeEvent::class.java) }.forEach {
                    // Loop through all methods which have the SubscribeEvent annotation and register them
                        method -> diService.invokeMethod<Listeners>(method, listenerClass).register(reactiveEventManager)
                }
            }

        return reactiveEventManager
    }
}
