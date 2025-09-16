package tytoo.weave.profile;

import tytoo.weave.component.Component;

import tytoo.weave.component.RenderStage;
import tytoo.weave.effects.Effect;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class FrameProfiler {
    private final int frameWindow;
    private final long logIntervalNanos;
    private long frameStart;
    private long currentMeasureNanos;
    private long currentArrangeNanos;
    private long currentAnimatorNanos;
    private long currentDrawNanos;
    private long currentOverlayNanos;
    private long currentTooltipNanos;
    private long currentToastNanos;
    private long currentPopupNanos;
    private int currentLayoutPasses;
    private int currentComponents;
    private int currentMeasureCalls;
    private long sumFrameNanos;
    private long sumMeasureNanos;
    private long sumArrangeNanos;
    private long sumAnimatorNanos;
    private long sumDrawNanos;
    private long sumOverlayNanos;
    private long sumTooltipNanos;
    private long sumToastNanos;
    private long sumPopupNanos;
    private long sumLayoutPasses;
    private long sumComponents;
    private long sumMeasureCalls;
    private int frames;
    private long lastPrintNano;
    private final Map<String, Long> componentDrawNanos = new HashMap<>();
    private final Map<String, Integer> componentDrawCounts = new HashMap<>();
    private final Deque<FrameCall> componentStack = new ArrayDeque<>();
    private final Map<StageKey, StageStat> componentStageStats = new HashMap<>();
    private final Map<EffectKey, StageStat> effectStats = new HashMap<>();

    public FrameProfiler(int frameWindow, long logIntervalNanos) {
        this.frameWindow = frameWindow;
        this.logIntervalNanos = logIntervalNanos;
    }

    public void beginFrame() {
        frameStart = System.nanoTime();
        currentMeasureNanos = 0L;
        currentArrangeNanos = 0L;
        currentAnimatorNanos = 0L;
        currentDrawNanos = 0L;
        currentOverlayNanos = 0L;
        currentTooltipNanos = 0L;
        currentToastNanos = 0L;
        currentPopupNanos = 0L;
        currentLayoutPasses = 0;
        currentComponents = 0;
        currentMeasureCalls = 0;
        componentStack.clear();
    }

    public void recordMeasure(long nanos) {
        currentMeasureNanos += nanos;
    }

    public void recordArrange(long nanos) {
        currentArrangeNanos += nanos;
    }

    public void recordAnimator(long nanos) {
        currentAnimatorNanos += nanos;
    }

    public void recordDraw(long nanos) {
        currentDrawNanos += nanos;
    }

    public void recordOverlay(long nanos) {
        currentOverlayNanos += nanos;
    }

    public void recordTooltip(long nanos) {
        currentTooltipNanos += nanos;
    }

    public void recordToast(long nanos) {
        currentToastNanos += nanos;
    }

    public void recordPopup(long nanos) {
        currentPopupNanos += nanos;
    }

    public void incrementLayoutPass() {
        currentLayoutPasses++;
    }

    public void onComponentMeasure() {
        currentMeasureCalls++;
    }

    public void endFrame() {
        long frameDuration = System.nanoTime() - frameStart;
        sumFrameNanos += frameDuration;
        sumMeasureNanos += currentMeasureNanos;
        sumArrangeNanos += currentArrangeNanos;
        sumAnimatorNanos += currentAnimatorNanos;
        sumDrawNanos += currentDrawNanos;
        sumOverlayNanos += currentOverlayNanos;
        sumTooltipNanos += currentTooltipNanos;
        sumToastNanos += currentToastNanos;
        sumPopupNanos += currentPopupNanos;
        sumLayoutPasses += currentLayoutPasses;
        sumComponents += currentComponents;
        sumMeasureCalls += currentMeasureCalls;
        frames++;
        long now = System.nanoTime();
        if (lastPrintNano == 0L) {
            lastPrintNano = frameStart;
        }
        if ((frameWindow > 0 && frames >= frameWindow) || (logIntervalNanos > 0 && now - lastPrintNano >= logIntervalNanos)) {
            printAndReset(now);
        }
    }

    public Object beginComponentDraw(Component<?> component) {
        FrameCall call = new FrameCall(component, System.nanoTime());
        componentStack.push(call);
        return call;
    }

    public void endComponentDraw(Component<?> component, Object token) {
        if (!(token instanceof FrameCall call)) {
            return;
        }
        FrameCall current = componentStack.isEmpty() ? null : componentStack.pop();
        if (current != call && current != null) {
            call = current;
        }
        long end = System.nanoTime();
        long duration = end - call.startNano;
        long exclusive = duration - call.childNanos;
        if (exclusive < 0L) {
            exclusive = 0L;
        }
        currentComponents++;
        String key = component != null ? component.getClass().getSimpleName() : "<null>";
        componentDrawNanos.merge(key, exclusive, Long::sum);
        componentDrawCounts.merge(key, 1, Integer::sum);
        FrameCall parent = componentStack.peek();
        if (parent != null) {
            parent.childNanos += duration;
        }
    }

    public void recordComponentStage(Component<?> component, RenderStage stage, long nanos) {
        if (stage == null || nanos < 0L) {
            return;
        }
        String componentName = component != null ? component.getClass().getSimpleName() : "<null>";
        StageKey key = new StageKey(componentName, stage);
        StageStat stat = componentStageStats.computeIfAbsent(key, k -> new StageStat());
        stat.add(nanos);
    }

    public void recordEffect(Component<?> component, Effect effect, EffectPhase phase, long nanos) {
        if (effect == null || phase == null || nanos < 0L) {
            return;
        }
        String componentName = component != null ? component.getClass().getSimpleName() : "<null>";
        String effectName = effect.getClass().getSimpleName();
        EffectKey key = new EffectKey(componentName, effectName, phase);
        StageStat stat = effectStats.computeIfAbsent(key, k -> new StageStat());
        stat.add(nanos);
    }

    private void printAndReset(long now) {
        if (frames == 0) {
            lastPrintNano = now;
            return;
        }
        double inv = 1.0 / frames;
        String topComponents = formatTopComponents();
        String topStages = formatTopStages();
        String topEffects = formatTopEffects();
        System.out.printf(Locale.ROOT,
                "[WeavePerf] frames=%d frame=%.3fms measure=%.3fms arrange=%.3fms layoutPasses=%.2f animator=%.3fms draw=%.3fms overlay=%.3fms tooltip=%.3fms toast=%.3fms popup=%.3fms components=%.1f measures=%.1f top=%s stage=%s effects=%s%n",
                frames,
                sumFrameNanos * inv / 1_000_000.0,
                sumMeasureNanos * inv / 1_000_000.0,
                sumArrangeNanos * inv / 1_000_000.0,
                sumLayoutPasses * inv,
                sumAnimatorNanos * inv / 1_000_000.0,
                sumDrawNanos * inv / 1_000_000.0,
                sumOverlayNanos * inv / 1_000_000.0,
                sumTooltipNanos * inv / 1_000_000.0,
                sumToastNanos * inv / 1_000_000.0,
                sumPopupNanos * inv / 1_000_000.0,
                sumComponents * inv,
                sumMeasureCalls * inv,
                topComponents,
                topStages,
                topEffects
        );
        sumFrameNanos = 0L;
        sumMeasureNanos = 0L;
        sumArrangeNanos = 0L;
        sumAnimatorNanos = 0L;
        sumDrawNanos = 0L;
        sumOverlayNanos = 0L;
        sumTooltipNanos = 0L;
        sumToastNanos = 0L;
        sumPopupNanos = 0L;
        sumLayoutPasses = 0L;
        sumComponents = 0L;
        sumMeasureCalls = 0L;
        frames = 0;
        lastPrintNano = now;
        componentDrawNanos.clear();
        componentDrawCounts.clear();
        componentStageStats.clear();
        effectStats.clear();
    }

    private String formatTopComponents() {
        if (componentDrawNanos.isEmpty()) {
            return "-";
        }
        List<Map.Entry<String, Long>> entries = new ArrayList<>(componentDrawNanos.entrySet());
        entries.sort((a, b) -> Long.compare(b.getValue(), a.getValue()));
        int limit = Math.min(3, entries.size());
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < limit; i++) {
            Map.Entry<String, Long> entry = entries.get(i);
            if (i > 0) {
                builder.append(' ');
            }
            String key = entry.getKey();
            long nanos = entry.getValue();
            int count = componentDrawCounts.getOrDefault(key, 0);
            double avgMillis = count > 0 ? (nanos / (double) count) / 1_000_000.0 : 0.0;
            builder.append(key)
                    .append('=')
                    .append(String.format(Locale.ROOT, "%.3fms", avgMillis))
                    .append("/draw(")
                    .append(count)
                    .append('x')
                    .append(')');
        }
        return builder.toString();
    }

    private String formatTopStages() {
        if (componentStageStats.isEmpty()) {
            return "-";
        }
        List<Map.Entry<StageKey, StageStat>> entries = new ArrayList<>(componentStageStats.entrySet());
        entries.sort((a, b) -> Double.compare(b.getValue().averageMillis(), a.getValue().averageMillis()));
        int limit = Math.min(3, entries.size());
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < limit; i++) {
            Map.Entry<StageKey, StageStat> entry = entries.get(i);
            if (i > 0) {
                builder.append(' ');
            }
            StageKey key = entry.getKey();
            StageStat stat = entry.getValue();
            builder.append(key.component())
                    .append("::")
                    .append(key.stage().name())
                    .append('=')
                    .append(String.format(Locale.ROOT, "%.3fms", stat.averageMillis()));
        }
        return builder.toString();
    }

    private String formatTopEffects() {
        if (effectStats.isEmpty()) {
            return "-";
        }
        List<Map.Entry<EffectKey, StageStat>> entries = new ArrayList<>(effectStats.entrySet());
        entries.sort((a, b) -> Double.compare(b.getValue().averageMillis(), a.getValue().averageMillis()));
        int limit = Math.min(3, entries.size());
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < limit; i++) {
            Map.Entry<EffectKey, StageStat> entry = entries.get(i);
            if (i > 0) {
                builder.append(' ');
            }
            EffectKey key = entry.getKey();
            StageStat stat = entry.getValue();
            builder.append(key.component())
                    .append("::")
                    .append(key.effect())
                    .append('@')
                    .append(key.phase().name())
                    .append('=')
                    .append(String.format(Locale.ROOT, "%.3fms", stat.averageMillis()));
        }
        return builder.toString();
    }

    public enum EffectPhase {
        BEFORE,
        AFTER
    }

    private record StageKey(String component, RenderStage stage) {
    }

    private record EffectKey(String component, String effect, EffectPhase phase) {
    }

    private static final class StageStat {
        private long nanos;
        private int count;

        void add(long delta) {
            nanos += delta;
            count++;
        }

        double averageMillis() {
            return count > 0 ? (nanos / (double) count) / 1_000_000.0 : 0.0;
        }
    }

    private static final class FrameCall {
        final Component<?> component;
        final long startNano;
        long childNanos;

        FrameCall(Component<?> component, long startNano) {
            this.component = component;
            this.startNano = startNano;
            this.childNanos = 0L;
        }
    }
}
