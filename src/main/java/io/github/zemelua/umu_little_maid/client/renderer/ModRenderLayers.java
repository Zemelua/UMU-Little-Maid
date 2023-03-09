package io.github.zemelua.umu_little_maid.client.renderer;

import net.minecraft.client.render.*;

public final class ModRenderLayers extends RenderLayer {
	public static final RenderLayer INSTRUCTION_TARGET = RenderLayer.of(
			"instruction_target", VertexFormats.POSITION_TEXTURE, VertexFormat.DrawMode.QUADS, RenderLayer.TRANSLUCENT_BUFFER_SIZE, false, false,
			RenderLayer.MultiPhaseParameters.builder()
					.layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
					.shader(RenderPhase.POSITION_TEXTURE_SHADER)
					.texture(BLOCK_ATLAS_TEXTURE)
					.transparency(TRANSLUCENT_TRANSPARENCY)
					.build(false));
	public static final RenderLayer INSTRUCTION_SITE = RenderLayer.of(
			"instruction_site", VertexFormats.POSITION_TEXTURE, VertexFormat.DrawMode.QUADS, RenderLayer.TRANSLUCENT_BUFFER_SIZE, false, false,
			RenderLayer.MultiPhaseParameters.builder()
					.layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
					.depthTest(RenderPhase.ALWAYS_DEPTH_TEST)
					.shader(RenderPhase.POSITION_TEXTURE_SHADER)
					.texture(BLOCK_ATLAS_TEXTURE)
					.transparency(TRANSLUCENT_TRANSPARENCY)
					.build(false));

	private ModRenderLayers(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
		super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
	}
}
