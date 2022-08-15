package net.wojteksz128.worktimemeasureserver.web.greeting;

import net.wojteksz128.worktimemeasureserver.api.version.VersionedResource;
import net.wojteksz128.worktimemeasureserver.web.ApiVersion;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

@RestController
@VersionedResource(media="application/vnd.net.wojteksz128.greeting")
public class GreetingController {

    public static final String template = "Hello, %s in World %s!";
    private final AtomicLong counter = new AtomicLong();

    @GetMapping("/greeting")
    @VersionedResource(from = ApiVersion.V1_0/*, to = ApiVersion.V1_0*/)
    public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return new Greeting(counter.incrementAndGet(), String.format(template, name, ApiVersion.V1_0));
    }

    @GetMapping(value = "/greeting")
    @VersionedResource(from = ApiVersion.V2_0)
    public Greeting greetingV2(@RequestParam(value = "name", defaultValue = "World") String name) {
        return new Greeting(counter.incrementAndGet(), String.format(template, name, ApiVersion.V2_0));
    }
}
