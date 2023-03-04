package io.github.zemelua.umu_little_maid.client.renderer;

import net.minecraft.client.render.*;

public final class ModRenderLayers extends RenderLayer {

	// protected static final Shader RENDERTYPE_PING = new RenderStateShard.ShaderStateShard(ClientHandler::getRenderTypePing);

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

	public static RenderLayer getIn() {
		return RenderLayer.of(
				"instruction_target", VertexFormats.POSITION_TEXTURE_COLOR, VertexFormat.DrawMode.QUADS, RenderLayer.TRANSLUCENT_BUFFER_SIZE, false, false,
				RenderLayer.MultiPhaseParameters.builder()
						.shader(new Shader(GameRenderer::getPositionTexColorShader))
						.texture(BLOCK_ATLAS_TEXTURE)
						// .layering(new RenderPhase.Layering("disable_depth", GlStateManager::_disableDepthTest, GlStateManager::_enableDepthTest))
						.transparency(TRANSLUCENT_TRANSPARENCY)
						.build(false));
	}

	public static RenderLayer getR() {
		return RenderLayer.of(
				"instruction_target", VertexFormats.POSITION_TEXTURE, VertexFormat.DrawMode.QUADS, RenderLayer.TRANSLUCENT_BUFFER_SIZE, false, false,
				RenderLayer.MultiPhaseParameters.builder()
						.layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
						.depthTest(RenderPhase.ALWAYS_DEPTH_TEST)
						.shader(RenderPhase.POSITION_TEXTURE_SHADER)
						.texture(BLOCK_ATLAS_TEXTURE)
						.transparency(TRANSLUCENT_TRANSPARENCY)
						.build(false));
	}

	private ModRenderLayers(String name, VertexFormat vertexFormat, VertexFormat.DrawMode drawMode, int expectedBufferSize, boolean hasCrumbling, boolean translucent, Runnable startAction, Runnable endAction) {
		super(name, vertexFormat, drawMode, expectedBufferSize, hasCrumbling, translucent, startAction, endAction);
	}
}
