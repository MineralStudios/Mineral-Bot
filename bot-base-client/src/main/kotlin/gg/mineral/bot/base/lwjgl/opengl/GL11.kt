package gg.mineral.bot.base.lwjgl.opengl

import gg.mineral.bot.impl.config.BotGlobalConfig
import org.lwjgl.opengl.GL11
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.nio.ShortBuffer

object GL11 {
    const val GL_ACCUM: Int = 0x100
    const val GL_LOAD: Int = 0x101
    const val GL_RETURN: Int = 0x102
    const val GL_MULT: Int = 0x103
    const val GL_ADD: Int = 0x104
    const val GL_NEVER: Int = 0x200
    const val GL_LESS: Int = 0x201
    const val GL_EQUAL: Int = 0x202
    const val GL_LEQUAL: Int = 0x203
    const val GL_GREATER: Int = 0x204
    const val GL_NOTEQUAL: Int = 0x205
    const val GL_GEQUAL: Int = 0x206
    const val GL_ALWAYS: Int = 0x207
    const val GL_CURRENT_BIT: Int = 0x1
    const val GL_POINT_BIT: Int = 0x2
    const val GL_LINE_BIT: Int = 0x4
    const val GL_POLYGON_BIT: Int = 0x8
    const val GL_POLYGON_STIPPLE_BIT: Int = 0x10
    const val GL_PIXEL_MODE_BIT: Int = 0x20
    const val GL_LIGHTING_BIT: Int = 0x40
    const val GL_FOG_BIT: Int = 0x80
    const val GL_DEPTH_BUFFER_BIT: Int = 0x100
    const val GL_ACCUM_BUFFER_BIT: Int = 0x200
    const val GL_STENCIL_BUFFER_BIT: Int = 0x400
    const val GL_VIEWPORT_BIT: Int = 0x800
    const val GL_TRANSFORM_BIT: Int = 0x1000
    const val GL_ENABLE_BIT: Int = 0x2000
    const val GL_COLOR_BUFFER_BIT: Int = 0x4000
    const val GL_HINT_BIT: Int = 0x8000
    const val GL_EVAL_BIT: Int = 0x10000
    const val GL_LIST_BIT: Int = 0x20000
    const val GL_TEXTURE_BIT: Int = 0x40000
    const val GL_SCISSOR_BIT: Int = 0x80000
    const val GL_ALL_ATTRIB_BITS: Int = 0xFFFFF
    const val GL_POINTS: Int = 0x0
    const val GL_LINES: Int = 0x1
    const val GL_LINE_LOOP: Int = 0x2
    const val GL_LINE_STRIP: Int = 0x3
    const val GL_TRIANGLES: Int = 0x4
    const val GL_TRIANGLE_STRIP: Int = 0x5
    const val GL_TRIANGLE_FAN: Int = 0x6
    const val GL_QUADS: Int = 0x7
    const val GL_QUAD_STRIP: Int = 0x8
    const val GL_POLYGON: Int = 0x9
    const val GL_ZERO: Int = 0x0
    const val GL_ONE: Int = 0x1
    const val GL_SRC_COLOR: Int = 0x300
    const val GL_ONE_MINUS_SRC_COLOR: Int = 0x301
    const val GL_SRC_ALPHA: Int = 0x302
    const val GL_ONE_MINUS_SRC_ALPHA: Int = 0x303
    const val GL_DST_ALPHA: Int = 0x304
    const val GL_ONE_MINUS_DST_ALPHA: Int = 0x305
    const val GL_DST_COLOR: Int = 0x306
    const val GL_ONE_MINUS_DST_COLOR: Int = 0x307
    const val GL_SRC_ALPHA_SATURATE: Int = 0x308
    const val GL_CONSTANT_COLOR: Int = 0x8001
    const val GL_ONE_MINUS_CONSTANT_COLOR: Int = 0x8002
    const val GL_CONSTANT_ALPHA: Int = 0x8003
    const val GL_ONE_MINUS_CONSTANT_ALPHA: Int = 0x8004
    const val GL_TRUE: Int = 0x1
    const val GL_FALSE: Int = 0x0
    const val GL_CLIP_PLANE0: Int = 0x3000
    const val GL_CLIP_PLANE1: Int = 0x3001
    const val GL_CLIP_PLANE2: Int = 0x3002
    const val GL_CLIP_PLANE3: Int = 0x3003
    const val GL_CLIP_PLANE4: Int = 0x3004
    const val GL_CLIP_PLANE5: Int = 0x3005
    const val GL_BYTE: Int = 0x1400
    const val GL_UNSIGNED_BYTE: Int = 0x1401
    const val GL_SHORT: Int = 0x1402
    const val GL_UNSIGNED_SHORT: Int = 0x1403
    const val GL_INT: Int = 0x1404
    const val GL_UNSIGNED_INT: Int = 0x1405
    const val GL_FLOAT: Int = 0x1406
    const val GL_2_BYTES: Int = 0x1407
    const val GL_3_BYTES: Int = 0x1408
    const val GL_4_BYTES: Int = 0x1409
    const val GL_DOUBLE: Int = 0x140A
    const val GL_NONE: Int = 0x0
    const val GL_FRONT_LEFT: Int = 0x400
    const val GL_FRONT_RIGHT: Int = 0x401
    const val GL_BACK_LEFT: Int = 0x402
    const val GL_BACK_RIGHT: Int = 0x403
    const val GL_FRONT: Int = 0x404
    const val GL_BACK: Int = 0x405
    const val GL_LEFT: Int = 0x406
    const val GL_RIGHT: Int = 0x407
    const val GL_FRONT_AND_BACK: Int = 0x408
    const val GL_AUX0: Int = 0x409
    const val GL_AUX1: Int = 0x40A
    const val GL_AUX2: Int = 0x40B
    const val GL_AUX3: Int = 0x40C
    const val GL_NO_ERROR: Int = 0x0
    const val GL_INVALID_ENUM: Int = 0x500
    const val GL_INVALID_VALUE: Int = 0x501
    const val GL_INVALID_OPERATION: Int = 0x502
    const val GL_STACK_OVERFLOW: Int = 0x503
    const val GL_STACK_UNDERFLOW: Int = 0x504
    const val GL_OUT_OF_MEMORY: Int = 0x505
    const val GL_2D: Int = 0x600
    const val GL_3D: Int = 0x601
    const val GL_3D_COLOR: Int = 0x602
    const val GL_3D_COLOR_TEXTURE: Int = 0x603
    const val GL_4D_COLOR_TEXTURE: Int = 0x604
    const val GL_PASS_THROUGH_TOKEN: Int = 0x700
    const val GL_POINT_TOKEN: Int = 0x701
    const val GL_LINE_TOKEN: Int = 0x702
    const val GL_POLYGON_TOKEN: Int = 0x703
    const val GL_BITMAP_TOKEN: Int = 0x704
    const val GL_DRAW_PIXEL_TOKEN: Int = 0x705
    const val GL_COPY_PIXEL_TOKEN: Int = 0x706
    const val GL_LINE_RESET_TOKEN: Int = 0x707
    const val GL_EXP: Int = 0x800
    const val GL_EXP2: Int = 0x801
    const val GL_CW: Int = 0x900
    const val GL_CCW: Int = 0x901
    const val GL_COEFF: Int = 0xA00
    const val GL_ORDER: Int = 0xA01
    const val GL_DOMAIN: Int = 0xA02
    const val GL_CURRENT_COLOR: Int = 0xB00
    const val GL_CURRENT_INDEX: Int = 0xB01
    const val GL_CURRENT_NORMAL: Int = 0xB02
    const val GL_CURRENT_TEXTURE_COORDS: Int = 0xB03
    const val GL_CURRENT_RASTER_COLOR: Int = 0xB04
    const val GL_CURRENT_RASTER_INDEX: Int = 0xB05
    const val GL_CURRENT_RASTER_TEXTURE_COORDS: Int = 0xB06
    const val GL_CURRENT_RASTER_POSITION: Int = 0xB07
    const val GL_CURRENT_RASTER_POSITION_VALID: Int = 0xB08
    const val GL_CURRENT_RASTER_DISTANCE: Int = 0xB09
    const val GL_POINT_SMOOTH: Int = 0xB10
    const val GL_POINT_SIZE: Int = 0xB11
    const val GL_POINT_SIZE_RANGE: Int = 0xB12
    const val GL_POINT_SIZE_GRANULARITY: Int = 0xB13
    const val GL_LINE_SMOOTH: Int = 0xB20
    const val GL_LINE_WIDTH: Int = 0xB21
    const val GL_LINE_WIDTH_RANGE: Int = 0xB22
    const val GL_LINE_WIDTH_GRANULARITY: Int = 0xB23
    const val GL_LINE_STIPPLE: Int = 0xB24
    const val GL_LINE_STIPPLE_PATTERN: Int = 0xB25
    const val GL_LINE_STIPPLE_REPEAT: Int = 0xB26
    const val GL_LIST_MODE: Int = 0xB30
    const val GL_MAX_LIST_NESTING: Int = 0xB31
    const val GL_LIST_BASE: Int = 0xB32
    const val GL_LIST_INDEX: Int = 0xB33
    const val GL_POLYGON_MODE: Int = 0xB40
    const val GL_POLYGON_SMOOTH: Int = 0xB41
    const val GL_POLYGON_STIPPLE: Int = 0xB42
    const val GL_EDGE_FLAG: Int = 0xB43
    const val GL_CULL_FACE: Int = 0xB44
    const val GL_CULL_FACE_MODE: Int = 0xB45
    const val GL_FRONT_FACE: Int = 0xB46
    const val GL_LIGHTING: Int = 0xB50
    const val GL_LIGHT_MODEL_LOCAL_VIEWER: Int = 0xB51
    const val GL_LIGHT_MODEL_TWO_SIDE: Int = 0xB52
    const val GL_LIGHT_MODEL_AMBIENT: Int = 0xB53
    const val GL_SHADE_MODEL: Int = 0xB54
    const val GL_COLOR_MATERIAL_FACE: Int = 0xB55
    const val GL_COLOR_MATERIAL_PARAMETER: Int = 0xB56
    const val GL_COLOR_MATERIAL: Int = 0xB57
    const val GL_FOG: Int = 0xB60
    const val GL_FOG_INDEX: Int = 0xB61
    const val GL_FOG_DENSITY: Int = 0xB62
    const val GL_FOG_START: Int = 0xB63
    const val GL_FOG_END: Int = 0xB64
    const val GL_FOG_MODE: Int = 0xB65
    const val GL_FOG_COLOR: Int = 0xB66
    const val GL_DEPTH_RANGE: Int = 0xB70
    const val GL_DEPTH_TEST: Int = 0xB71
    const val GL_DEPTH_WRITEMASK: Int = 0xB72
    const val GL_DEPTH_CLEAR_VALUE: Int = 0xB73
    const val GL_DEPTH_FUNC: Int = 0xB74
    const val GL_ACCUM_CLEAR_VALUE: Int = 0xB80
    const val GL_STENCIL_TEST: Int = 0xB90
    const val GL_STENCIL_CLEAR_VALUE: Int = 0xB91
    const val GL_STENCIL_FUNC: Int = 0xB92
    const val GL_STENCIL_VALUE_MASK: Int = 0xB93
    const val GL_STENCIL_FAIL: Int = 0xB94
    const val GL_STENCIL_PASS_DEPTH_FAIL: Int = 0xB95
    const val GL_STENCIL_PASS_DEPTH_PASS: Int = 0xB96
    const val GL_STENCIL_REF: Int = 0xB97
    const val GL_STENCIL_WRITEMASK: Int = 0xB98
    const val GL_MATRIX_MODE: Int = 0xBA0
    const val GL_NORMALIZE: Int = 0xBA1
    const val GL_VIEWPORT: Int = 0xBA2
    const val GL_MODELVIEW_STACK_DEPTH: Int = 0xBA3
    const val GL_PROJECTION_STACK_DEPTH: Int = 0xBA4
    const val GL_TEXTURE_STACK_DEPTH: Int = 0xBA5
    const val GL_MODELVIEW_MATRIX: Int = 0xBA6
    const val GL_PROJECTION_MATRIX: Int = 0xBA7
    const val GL_TEXTURE_MATRIX: Int = 0xBA8
    const val GL_ATTRIB_STACK_DEPTH: Int = 0xBB0
    const val GL_CLIENT_ATTRIB_STACK_DEPTH: Int = 0xBB1
    const val GL_ALPHA_TEST: Int = 0xBC0
    const val GL_ALPHA_TEST_FUNC: Int = 0xBC1
    const val GL_ALPHA_TEST_REF: Int = 0xBC2
    const val GL_DITHER: Int = 0xBD0
    const val GL_BLEND_DST: Int = 0xBE0
    const val GL_BLEND_SRC: Int = 0xBE1
    const val GL_BLEND: Int = 0xBE2
    const val GL_LOGIC_OP_MODE: Int = 0xBF0
    const val GL_INDEX_LOGIC_OP: Int = 0xBF1
    const val GL_COLOR_LOGIC_OP: Int = 0xBF2
    const val GL_AUX_BUFFERS: Int = 0xC00
    const val GL_DRAW_BUFFER: Int = 0xC01
    const val GL_READ_BUFFER: Int = 0xC02
    const val GL_SCISSOR_BOX: Int = 0xC10
    const val GL_SCISSOR_TEST: Int = 0xC11
    const val GL_INDEX_CLEAR_VALUE: Int = 0xC20
    const val GL_INDEX_WRITEMASK: Int = 0xC21
    const val GL_COLOR_CLEAR_VALUE: Int = 0xC22
    const val GL_COLOR_WRITEMASK: Int = 0xC23
    const val GL_INDEX_MODE: Int = 0xC30
    const val GL_RGBA_MODE: Int = 0xC31
    const val GL_DOUBLEBUFFER: Int = 0xC32
    const val GL_STEREO: Int = 0xC33
    const val GL_RENDER_MODE: Int = 0xC40
    const val GL_PERSPECTIVE_CORRECTION_HINT: Int = 0xC50
    const val GL_POINT_SMOOTH_HINT: Int = 0xC51
    const val GL_LINE_SMOOTH_HINT: Int = 0xC52
    const val GL_POLYGON_SMOOTH_HINT: Int = 0xC53
    const val GL_FOG_HINT: Int = 0xC54
    const val GL_TEXTURE_GEN_S: Int = 0xC60
    const val GL_TEXTURE_GEN_T: Int = 0xC61
    const val GL_TEXTURE_GEN_R: Int = 0xC62
    const val GL_TEXTURE_GEN_Q: Int = 0xC63
    const val GL_PIXEL_MAP_I_TO_I: Int = 0xC70
    const val GL_PIXEL_MAP_S_TO_S: Int = 0xC71
    const val GL_PIXEL_MAP_I_TO_R: Int = 0xC72
    const val GL_PIXEL_MAP_I_TO_G: Int = 0xC73
    const val GL_PIXEL_MAP_I_TO_B: Int = 0xC74
    const val GL_PIXEL_MAP_I_TO_A: Int = 0xC75
    const val GL_PIXEL_MAP_R_TO_R: Int = 0xC76
    const val GL_PIXEL_MAP_G_TO_G: Int = 0xC77
    const val GL_PIXEL_MAP_B_TO_B: Int = 0xC78
    const val GL_PIXEL_MAP_A_TO_A: Int = 0xC79
    const val GL_PIXEL_MAP_I_TO_I_SIZE: Int = 0xCB0
    const val GL_PIXEL_MAP_S_TO_S_SIZE: Int = 0xCB1
    const val GL_PIXEL_MAP_I_TO_R_SIZE: Int = 0xCB2
    const val GL_PIXEL_MAP_I_TO_G_SIZE: Int = 0xCB3
    const val GL_PIXEL_MAP_I_TO_B_SIZE: Int = 0xCB4
    const val GL_PIXEL_MAP_I_TO_A_SIZE: Int = 0xCB5
    const val GL_PIXEL_MAP_R_TO_R_SIZE: Int = 0xCB6
    const val GL_PIXEL_MAP_G_TO_G_SIZE: Int = 0xCB7
    const val GL_PIXEL_MAP_B_TO_B_SIZE: Int = 0xCB8
    const val GL_PIXEL_MAP_A_TO_A_SIZE: Int = 0xCB9
    const val GL_UNPACK_SWAP_BYTES: Int = 0xCF0
    const val GL_UNPACK_LSB_FIRST: Int = 0xCF1
    const val GL_UNPACK_ROW_LENGTH: Int = 0xCF2
    const val GL_UNPACK_SKIP_ROWS: Int = 0xCF3
    const val GL_UNPACK_SKIP_PIXELS: Int = 0xCF4
    const val GL_UNPACK_ALIGNMENT: Int = 0xCF5
    const val GL_PACK_SWAP_BYTES: Int = 0xD00
    const val GL_PACK_LSB_FIRST: Int = 0xD01
    const val GL_PACK_ROW_LENGTH: Int = 0xD02
    const val GL_PACK_SKIP_ROWS: Int = 0xD03
    const val GL_PACK_SKIP_PIXELS: Int = 0xD04
    const val GL_PACK_ALIGNMENT: Int = 0xD05
    const val GL_MAP_COLOR: Int = 0xD10
    const val GL_MAP_STENCIL: Int = 0xD11
    const val GL_INDEX_SHIFT: Int = 0xD12
    const val GL_INDEX_OFFSET: Int = 0xD13
    const val GL_RED_SCALE: Int = 0xD14
    const val GL_RED_BIAS: Int = 0xD15
    const val GL_ZOOM_X: Int = 0xD16
    const val GL_ZOOM_Y: Int = 0xD17
    const val GL_GREEN_SCALE: Int = 0xD18
    const val GL_GREEN_BIAS: Int = 0xD19
    const val GL_BLUE_SCALE: Int = 0xD1A
    const val GL_BLUE_BIAS: Int = 0xD1B
    const val GL_ALPHA_SCALE: Int = 0xD1C
    const val GL_ALPHA_BIAS: Int = 0xD1D
    const val GL_DEPTH_SCALE: Int = 0xD1E
    const val GL_DEPTH_BIAS: Int = 0xD1F
    const val GL_MAX_EVAL_ORDER: Int = 0xD30
    const val GL_MAX_LIGHTS: Int = 0xD31
    const val GL_MAX_CLIP_PLANES: Int = 0xD32
    const val GL_MAX_TEXTURE_SIZE: Int = 0xD33
    const val GL_MAX_PIXEL_MAP_TABLE: Int = 0xD34
    const val GL_MAX_ATTRIB_STACK_DEPTH: Int = 0xD35
    const val GL_MAX_MODELVIEW_STACK_DEPTH: Int = 0xD36
    const val GL_MAX_NAME_STACK_DEPTH: Int = 0xD37
    const val GL_MAX_PROJECTION_STACK_DEPTH: Int = 0xD38
    const val GL_MAX_TEXTURE_STACK_DEPTH: Int = 0xD39
    const val GL_MAX_VIEWPORT_DIMS: Int = 0xD3A
    const val GL_MAX_CLIENT_ATTRIB_STACK_DEPTH: Int = 0xD3B
    const val GL_SUBPIXEL_BITS: Int = 0xD50
    const val GL_INDEX_BITS: Int = 0xD51
    const val GL_RED_BITS: Int = 0xD52
    const val GL_GREEN_BITS: Int = 0xD53
    const val GL_BLUE_BITS: Int = 0xD54
    const val GL_ALPHA_BITS: Int = 0xD55
    const val GL_DEPTH_BITS: Int = 0xD56
    const val GL_STENCIL_BITS: Int = 0xD57
    const val GL_ACCUM_RED_BITS: Int = 0xD58
    const val GL_ACCUM_GREEN_BITS: Int = 0xD59
    const val GL_ACCUM_BLUE_BITS: Int = 0xD5A
    const val GL_ACCUM_ALPHA_BITS: Int = 0xD5B
    const val GL_NAME_STACK_DEPTH: Int = 0xD70
    const val GL_AUTO_NORMAL: Int = 0xD80
    const val GL_MAP1_COLOR_4: Int = 0xD90
    const val GL_MAP1_INDEX: Int = 0xD91
    const val GL_MAP1_NORMAL: Int = 0xD92
    const val GL_MAP1_TEXTURE_COORD_1: Int = 0xD93
    const val GL_MAP1_TEXTURE_COORD_2: Int = 0xD94
    const val GL_MAP1_TEXTURE_COORD_3: Int = 0xD95
    const val GL_MAP1_TEXTURE_COORD_4: Int = 0xD96
    const val GL_MAP1_VERTEX_3: Int = 0xD97
    const val GL_MAP1_VERTEX_4: Int = 0xD98
    const val GL_MAP2_COLOR_4: Int = 0xDB0
    const val GL_MAP2_INDEX: Int = 0xDB1
    const val GL_MAP2_NORMAL: Int = 0xDB2
    const val GL_MAP2_TEXTURE_COORD_1: Int = 0xDB3
    const val GL_MAP2_TEXTURE_COORD_2: Int = 0xDB4
    const val GL_MAP2_TEXTURE_COORD_3: Int = 0xDB5
    const val GL_MAP2_TEXTURE_COORD_4: Int = 0xDB6
    const val GL_MAP2_VERTEX_3: Int = 0xDB7
    const val GL_MAP2_VERTEX_4: Int = 0xDB8
    const val GL_MAP1_GRID_DOMAIN: Int = 0xDD0
    const val GL_MAP1_GRID_SEGMENTS: Int = 0xDD1
    const val GL_MAP2_GRID_DOMAIN: Int = 0xDD2
    const val GL_MAP2_GRID_SEGMENTS: Int = 0xDD3
    const val GL_TEXTURE_1D: Int = 0xDE0
    const val GL_TEXTURE_2D: Int = 0xDE1
    const val GL_FEEDBACK_BUFFER_POINTER: Int = 0xDF0
    const val GL_FEEDBACK_BUFFER_SIZE: Int = 0xDF1
    const val GL_FEEDBACK_BUFFER_TYPE: Int = 0xDF2
    const val GL_SELECTION_BUFFER_POINTER: Int = 0xDF3
    const val GL_SELECTION_BUFFER_SIZE: Int = 0xDF4
    const val GL_TEXTURE_WIDTH: Int = 0x1000
    const val GL_TEXTURE_HEIGHT: Int = 0x1001
    const val GL_TEXTURE_INTERNAL_FORMAT: Int = 0x1003
    const val GL_TEXTURE_BORDER_COLOR: Int = 0x1004
    const val GL_TEXTURE_BORDER: Int = 0x1005
    const val GL_DONT_CARE: Int = 0x1100
    const val GL_FASTEST: Int = 0x1101
    const val GL_NICEST: Int = 0x1102
    const val GL_LIGHT0: Int = 0x4000
    const val GL_LIGHT1: Int = 0x4001
    const val GL_LIGHT2: Int = 0x4002
    const val GL_LIGHT3: Int = 0x4003
    const val GL_LIGHT4: Int = 0x4004
    const val GL_LIGHT5: Int = 0x4005
    const val GL_LIGHT6: Int = 0x4006
    const val GL_LIGHT7: Int = 0x4007
    const val GL_AMBIENT: Int = 0x1200
    const val GL_DIFFUSE: Int = 0x1201
    const val GL_SPECULAR: Int = 0x1202
    const val GL_POSITION: Int = 0x1203
    const val GL_SPOT_DIRECTION: Int = 0x1204
    const val GL_SPOT_EXPONENT: Int = 0x1205
    const val GL_SPOT_CUTOFF: Int = 0x1206
    const val GL_CONSTANT_ATTENUATION: Int = 0x1207
    const val GL_LINEAR_ATTENUATION: Int = 0x1208
    const val GL_QUADRATIC_ATTENUATION: Int = 0x1209
    const val GL_COMPILE: Int = 0x1300
    const val GL_COMPILE_AND_EXECUTE: Int = 0x1301
    const val GL_CLEAR: Int = 0x1500
    const val GL_AND: Int = 0x1501
    const val GL_AND_REVERSE: Int = 0x1502
    const val GL_COPY: Int = 0x1503
    const val GL_AND_INVERTED: Int = 0x1504
    const val GL_NOOP: Int = 0x1505
    const val GL_XOR: Int = 0x1506
    const val GL_OR: Int = 0x1507
    const val GL_NOR: Int = 0x1508
    const val GL_EQUIV: Int = 0x1509
    const val GL_INVERT: Int = 0x150A
    const val GL_OR_REVERSE: Int = 0x150B
    const val GL_COPY_INVERTED: Int = 0x150C
    const val GL_OR_INVERTED: Int = 0x150D
    const val GL_NAND: Int = 0x150E
    const val GL_SET: Int = 0x150F
    const val GL_EMISSION: Int = 0x1600
    const val GL_SHININESS: Int = 0x1601
    const val GL_AMBIENT_AND_DIFFUSE: Int = 0x1602
    const val GL_COLOR_INDEXES: Int = 0x1603
    const val GL_MODELVIEW: Int = 0x1700
    const val GL_PROJECTION: Int = 0x1701
    const val GL_TEXTURE: Int = 0x1702
    const val GL_COLOR: Int = 0x1800
    const val GL_DEPTH: Int = 0x1801
    const val GL_STENCIL: Int = 0x1802
    const val GL_COLOR_INDEX: Int = 0x1900
    const val GL_STENCIL_INDEX: Int = 0x1901
    const val GL_DEPTH_COMPONENT: Int = 0x1902
    const val GL_RED: Int = 0x1903
    const val GL_GREEN: Int = 0x1904
    const val GL_BLUE: Int = 0x1905
    const val GL_ALPHA: Int = 0x1906
    const val GL_RGB: Int = 0x1907
    const val GL_RGBA: Int = 0x1908
    const val GL_LUMINANCE: Int = 0x1909
    const val GL_LUMINANCE_ALPHA: Int = 0x190A
    const val GL_BITMAP: Int = 0x1A00
    const val GL_POINT: Int = 0x1B00
    const val GL_LINE: Int = 0x1B01
    const val GL_FILL: Int = 0x1B02
    const val GL_RENDER: Int = 0x1C00
    const val GL_FEEDBACK: Int = 0x1C01
    const val GL_SELECT: Int = 0x1C02
    const val GL_FLAT: Int = 0x1D00
    const val GL_SMOOTH: Int = 0x1D01
    const val GL_KEEP: Int = 0x1E00
    const val GL_REPLACE: Int = 0x1E01
    const val GL_INCR: Int = 0x1E02
    const val GL_DECR: Int = 0x1E03
    const val GL_VENDOR: Int = 0x1F00
    const val GL_RENDERER: Int = 0x1F01
    const val GL_VERSION: Int = 0x1F02
    const val GL_EXTENSIONS: Int = 0x1F03
    const val GL_S: Int = 0x2000
    const val GL_T: Int = 0x2001
    const val GL_R: Int = 0x2002
    const val GL_Q: Int = 0x2003
    const val GL_MODULATE: Int = 0x2100
    const val GL_DECAL: Int = 0x2101
    const val GL_TEXTURE_ENV_MODE: Int = 0x2200
    const val GL_TEXTURE_ENV_COLOR: Int = 0x2201
    const val GL_TEXTURE_ENV: Int = 0x2300
    const val GL_EYE_LINEAR: Int = 0x2400
    const val GL_OBJECT_LINEAR: Int = 0x2401
    const val GL_SPHERE_MAP: Int = 0x2402
    const val GL_TEXTURE_GEN_MODE: Int = 0x2500
    const val GL_OBJECT_PLANE: Int = 0x2501
    const val GL_EYE_PLANE: Int = 0x2502
    const val GL_NEAREST: Int = 0x2600
    const val GL_LINEAR: Int = 0x2601
    const val GL_NEAREST_MIPMAP_NEAREST: Int = 0x2700
    const val GL_LINEAR_MIPMAP_NEAREST: Int = 0x2701
    const val GL_NEAREST_MIPMAP_LINEAR: Int = 0x2702
    const val GL_LINEAR_MIPMAP_LINEAR: Int = 0x2703
    const val GL_TEXTURE_MAG_FILTER: Int = 0x2800
    const val GL_TEXTURE_MIN_FILTER: Int = 0x2801
    const val GL_TEXTURE_WRAP_S: Int = 0x2802
    const val GL_TEXTURE_WRAP_T: Int = 0x2803
    const val GL_CLAMP: Int = 0x2900
    const val GL_REPEAT: Int = 0x2901
    const val GL_CLIENT_PIXEL_STORE_BIT: Int = 0x1
    const val GL_CLIENT_VERTEX_ARRAY_BIT: Int = 0x2
    const val GL_ALL_CLIENT_ATTRIB_BITS: Int = -0x1
    const val GL_POLYGON_OFFSET_FACTOR: Int = 0x8038
    const val GL_POLYGON_OFFSET_UNITS: Int = 0x2A00
    const val GL_POLYGON_OFFSET_POINT: Int = 0x2A01
    const val GL_POLYGON_OFFSET_LINE: Int = 0x2A02
    const val GL_POLYGON_OFFSET_FILL: Int = 0x8037
    const val GL_ALPHA4: Int = 0x803B
    const val GL_ALPHA8: Int = 0x803C
    const val GL_ALPHA12: Int = 0x803D
    const val GL_ALPHA16: Int = 0x803E
    const val GL_LUMINANCE4: Int = 0x803F
    const val GL_LUMINANCE8: Int = 0x8040
    const val GL_LUMINANCE12: Int = 0x8041
    const val GL_LUMINANCE16: Int = 0x8042
    const val GL_LUMINANCE4_ALPHA4: Int = 0x8043
    const val GL_LUMINANCE6_ALPHA2: Int = 0x8044
    const val GL_LUMINANCE8_ALPHA8: Int = 0x8045
    const val GL_LUMINANCE12_ALPHA4: Int = 0x8046
    const val GL_LUMINANCE12_ALPHA12: Int = 0x8047
    const val GL_LUMINANCE16_ALPHA16: Int = 0x8048
    const val GL_INTENSITY: Int = 0x8049
    const val GL_INTENSITY4: Int = 0x804A
    const val GL_INTENSITY8: Int = 0x804B
    const val GL_INTENSITY12: Int = 0x804C
    const val GL_INTENSITY16: Int = 0x804D
    const val GL_R3_G3_B2: Int = 0x2A10
    const val GL_RGB4: Int = 0x804F
    const val GL_RGB5: Int = 0x8050
    const val GL_RGB8: Int = 0x8051
    const val GL_RGB10: Int = 0x8052
    const val GL_RGB12: Int = 0x8053
    const val GL_RGB16: Int = 0x8054
    const val GL_RGBA2: Int = 0x8055
    const val GL_RGBA4: Int = 0x8056
    const val GL_RGB5_A1: Int = 0x8057
    const val GL_RGBA8: Int = 0x8058
    const val GL_RGB10_A2: Int = 0x8059
    const val GL_RGBA12: Int = 0x805A
    const val GL_RGBA16: Int = 0x805B
    const val GL_TEXTURE_RED_SIZE: Int = 0x805C
    const val GL_TEXTURE_GREEN_SIZE: Int = 0x805D
    const val GL_TEXTURE_BLUE_SIZE: Int = 0x805E
    const val GL_TEXTURE_ALPHA_SIZE: Int = 0x805F
    const val GL_TEXTURE_LUMINANCE_SIZE: Int = 0x8060
    const val GL_TEXTURE_INTENSITY_SIZE: Int = 0x8061
    const val GL_PROXY_TEXTURE_1D: Int = 0x8063
    const val GL_PROXY_TEXTURE_2D: Int = 0x8064
    const val GL_TEXTURE_PRIORITY: Int = 0x8066
    const val GL_TEXTURE_RESIDENT: Int = 0x8067
    const val GL_TEXTURE_BINDING_1D: Int = 0x8068
    const val GL_TEXTURE_BINDING_2D: Int = 0x8069
    const val GL_VERTEX_ARRAY: Int = 0x8074
    const val GL_NORMAL_ARRAY: Int = 0x8075
    const val GL_COLOR_ARRAY: Int = 0x8076
    const val GL_INDEX_ARRAY: Int = 0x8077
    const val GL_TEXTURE_COORD_ARRAY: Int = 0x8078
    const val GL_EDGE_FLAG_ARRAY: Int = 0x8079
    const val GL_VERTEX_ARRAY_SIZE: Int = 0x807A
    const val GL_VERTEX_ARRAY_TYPE: Int = 0x807B
    const val GL_VERTEX_ARRAY_STRIDE: Int = 0x807C
    const val GL_NORMAL_ARRAY_TYPE: Int = 0x807E
    const val GL_NORMAL_ARRAY_STRIDE: Int = 0x807F
    const val GL_COLOR_ARRAY_SIZE: Int = 0x8081
    const val GL_COLOR_ARRAY_TYPE: Int = 0x8082
    const val GL_COLOR_ARRAY_STRIDE: Int = 0x8083
    const val GL_INDEX_ARRAY_TYPE: Int = 0x8085
    const val GL_INDEX_ARRAY_STRIDE: Int = 0x8086
    const val GL_TEXTURE_COORD_ARRAY_SIZE: Int = 0x8088
    const val GL_TEXTURE_COORD_ARRAY_TYPE: Int = 0x8089
    const val GL_TEXTURE_COORD_ARRAY_STRIDE: Int = 0x808A
    const val GL_EDGE_FLAG_ARRAY_STRIDE: Int = 0x808C
    const val GL_VERTEX_ARRAY_POINTER: Int = 0x808E
    const val GL_NORMAL_ARRAY_POINTER: Int = 0x808F
    const val GL_COLOR_ARRAY_POINTER: Int = 0x8090
    const val GL_INDEX_ARRAY_POINTER: Int = 0x8091
    const val GL_TEXTURE_COORD_ARRAY_POINTER: Int = 0x8092
    const val GL_EDGE_FLAG_ARRAY_POINTER: Int = 0x8093
    const val GL_V2F: Int = 0x2A20
    const val GL_V3F: Int = 0x2A21
    const val GL_C4UB_V2F: Int = 0x2A22
    const val GL_C4UB_V3F: Int = 0x2A23
    const val GL_C3F_V3F: Int = 0x2A24
    const val GL_N3F_V3F: Int = 0x2A25
    const val GL_C4F_N3F_V3F: Int = 0x2A26
    const val GL_T2F_V3F: Int = 0x2A27
    const val GL_T4F_V4F: Int = 0x2A28
    const val GL_T2F_C4UB_V3F: Int = 0x2A29
    const val GL_T2F_C3F_V3F: Int = 0x2A2A
    const val GL_T2F_N3F_V3F: Int = 0x2A2B
    const val GL_T2F_C4F_N3F_V3F: Int = 0x2A2C
    const val GL_T4F_C4F_N3F_V4F: Int = 0x2A2D
    const val GL_LOGIC_OP: Int = 0xBF1
    const val GL_TEXTURE_COMPONENTS: Int = 0x1003

    @JvmStatic
    fun glViewport(x: Int, y: Int, width: Int, height: Int) {
        if (BotGlobalConfig.headless) return

        GL11.glViewport(x, y, width, height)
    }

    @JvmStatic
    fun glMatrixMode(mode: Int) {
        if (BotGlobalConfig.headless) return

        GL11.glMatrixMode(mode)
    }

    @JvmStatic
    fun glLoadIdentity() {
        if (BotGlobalConfig.headless) return

        GL11.glLoadIdentity()
    }

    @JvmStatic
    fun glClear(mask: Int) {
        if (BotGlobalConfig.headless) return

        GL11.glClear(mask)
    }

    @JvmStatic
    fun glOrtho(left: Double, right: Double, bottom: Double, top: Double, zNear: Double, zFar: Double) {
        if (BotGlobalConfig.headless) return

        GL11.glOrtho(left, right, bottom, top, zNear, zFar)
    }

    @JvmStatic
    fun glTranslatef(x: Float, y: Float, z: Float) {
        if (BotGlobalConfig.headless) return

        GL11.glTranslatef(x, y, z)
    }

    @JvmStatic
    fun glDisable(cap: Int) {
        if (BotGlobalConfig.headless) return

        GL11.glDisable(cap)
    }

    @JvmStatic
    fun glColor4f(red: Float, green: Float, blue: Float, alpha: Float) {
        if (BotGlobalConfig.headless) return

        GL11.glColor4f(red, green, blue, alpha)
    }

    @JvmStatic
    fun glEnable(cap: Int) {
        if (BotGlobalConfig.headless) return

        GL11.glEnable(cap)
    }

    @JvmStatic
    fun glDepthMask(flag: Boolean) {
        if (BotGlobalConfig.headless) return

        GL11.glDepthMask(flag)
    }

    @JvmStatic
    fun glPushMatrix() {
        if (BotGlobalConfig.headless) return

        GL11.glPushMatrix()
    }

    @JvmStatic
    fun glDepthFunc(func: Int) {
        if (BotGlobalConfig.headless) return

        GL11.glDepthFunc(func)
    }

    @JvmStatic
    fun glScalef(x: Float, y: Float, z: Float) {
        if (BotGlobalConfig.headless) return

        GL11.glScalef(x, y, z)
    }

    @JvmStatic
    fun glBlendFunc(sfactor: Int, dfactor: Int) {
        if (BotGlobalConfig.headless) return

        GL11.glBlendFunc(sfactor, dfactor)
    }

    @JvmStatic
    fun glPopMatrix() {
        if (BotGlobalConfig.headless) return

        GL11.glPopMatrix()
    }

    @JvmStatic
    fun glBegin(mode: Int) {
        if (BotGlobalConfig.headless) return

        GL11.glBegin(mode)
    }

    @JvmStatic
    fun glTexCoord2f(s: Float, t: Float) {
        if (BotGlobalConfig.headless) return

        GL11.glTexCoord2f(s, t)
    }

    @JvmStatic
    fun glRotatef(angle: Float, x: Float, y: Float, z: Float) {
        if (BotGlobalConfig.headless) return

        GL11.glRotatef(angle, x, y, z)
    }

    @JvmStatic
    fun glVertex3f(x: Float, y: Float, z: Float) {
        if (BotGlobalConfig.headless) return

        GL11.glVertex3f(x, y, z)
    }

    @JvmStatic
    fun glEnd() {
        if (BotGlobalConfig.headless) return

        GL11.glEnd()
    }

    @JvmStatic
    fun glShadeModel(mode: Int) {
        if (BotGlobalConfig.headless) return

        GL11.glShadeModel(mode)
    }

    @JvmStatic
    fun glColor3f(red: Float, green: Float, blue: Float) {
        if (BotGlobalConfig.headless) return

        GL11.glColor3f(red, green, blue)
    }

    @JvmStatic
    fun glColorMask(red: Boolean, green: Boolean, blue: Boolean, alpha: Boolean) {
        if (BotGlobalConfig.headless) return

        GL11.glColorMask(red, green, blue, alpha)
    }

    @JvmStatic
    fun glTexParameteri(target: Int, pname: Int, param: Int) {
        if (BotGlobalConfig.headless) return

        GL11.glTexParameteri(target, pname, param)
    }

    @JvmStatic
    fun glCopyTexSubImage2D(
        target: Int, level: Int, xoffset: Int, yoffset: Int, x: Int, y: Int, width: Int,
        height: Int
    ) {
        if (BotGlobalConfig.headless) return

        GL11.glCopyTexSubImage2D(target, level, xoffset, yoffset, x, y, width, height)
    }

    @JvmStatic
    fun glEndList() {
        if (BotGlobalConfig.headless) return

        GL11.glEndList()
    }

    @JvmStatic
    fun glFogf(pname: Int, param: Float) {
        if (BotGlobalConfig.headless) return

        GL11.glFogf(pname, param)
    }

    @JvmStatic
    fun glLineWidth(width: Float) {
        if (BotGlobalConfig.headless) return

        GL11.glLineWidth(width)
    }

    @JvmStatic
    fun glLogicOp(opcode: Int) {
        if (BotGlobalConfig.headless) return

        GL11.glLogicOp(opcode)
    }

    @JvmStatic
    fun glClearDepth(depth: Double) {
        if (BotGlobalConfig.headless) return

        GL11.glClearDepth(depth)
    }

    @JvmStatic
    fun glAlphaFunc(func: Int, ref: Float) {
        if (BotGlobalConfig.headless) return

        GL11.glAlphaFunc(func, ref)
    }

    @JvmStatic
    fun glCullFace(mode: Int) {
        if (BotGlobalConfig.headless) return

        GL11.glCullFace(mode)
    }

    @JvmStatic
    fun glFlush() {
        if (BotGlobalConfig.headless) return

        GL11.glFlush()
    }

    @JvmStatic
    fun glGetError(): Int {
        if (BotGlobalConfig.headless) return 0

        return GL11.glGetError()
    }

    @JvmStatic
    fun glGetString(name: Int): String {
        if (BotGlobalConfig.headless) return ""

        return GL11.glGetString(name)
    }

    @JvmStatic
    fun glTexImage2D(
        target: Int, level: Int, internalformat: Int, width: Int, height: Int, border: Int,
        format: Int, type: Int, pixels: ByteBuffer?
    ) {
        if (BotGlobalConfig.headless) return

        GL11.glTexImage2D(target, level, internalformat, width, height, border, format, type, pixels)
    }

    @JvmStatic
    fun glGetTexLevelParameteri(target: Int, level: Int, pname: Int): Int {
        if (BotGlobalConfig.headless) return 1

        return GL11.glGetTexLevelParameteri(target, level, pname)
    }

    @JvmStatic
    fun glCallList(list: Int) {
        if (BotGlobalConfig.headless) return

        GL11.glCallList(list)
    }

    @JvmStatic
    fun glNewList(list: Int, mode: Int) {
        if (BotGlobalConfig.headless) return

        GL11.glNewList(list, mode)
    }

    @JvmStatic
    fun glPolygonOffset(factor: Float, units: Float) {
        if (BotGlobalConfig.headless) return

        GL11.glPolygonOffset(factor, units)
    }

    @JvmStatic
    fun glGetFloat(pname: Int, params: FloatBuffer) {
        if (BotGlobalConfig.headless) return

        GL11.glGetFloat(pname, params)
    }

    @JvmStatic
    fun glGetInteger(pname: Int, params: IntBuffer) {
        if (BotGlobalConfig.headless) return

        GL11.glGetInteger(pname, params)
    }

    @JvmStatic
    fun glNormal3f(nx: Float, ny: Float, nz: Float) {
        if (BotGlobalConfig.headless) return

        GL11.glNormal3f(nx, ny, nz)
    }

    @JvmStatic
    fun glTranslated(x: Double, y: Double, z: Double) {
        if (BotGlobalConfig.headless) return

        GL11.glTranslated(x, y, z)
    }

    @JvmStatic
    fun glScaled(x: Double, y: Double, z: Double) {
        if (BotGlobalConfig.headless) return

        GL11.glScaled(x, y, z)
    }

    @JvmStatic
    fun glClearColor(red: Float, green: Float, blue: Float, alpha: Float) {
        if (BotGlobalConfig.headless) return

        GL11.glClearColor(red, green, blue, alpha)
    }

    @JvmStatic
    fun glFog(pname: Int, params: FloatBuffer) {
        if (BotGlobalConfig.headless) return

        GL11.glFog(pname, params)
    }

    @JvmStatic
    fun glFogi(pname: Int, param: Int) {
        if (BotGlobalConfig.headless) return

        GL11.glFogi(pname, param)
    }

    @JvmStatic
    fun glColorMaterial(face: Int, mode: Int) {
        if (BotGlobalConfig.headless) return

        GL11.glColorMaterial(face, mode)
    }

    @JvmStatic
    fun glGenLists(range: Int): Int {
        if (BotGlobalConfig.headless) return 0

        return GL11.glGenLists(range)
    }

    @JvmStatic
    fun glDeleteLists(list: Int, range: Int) {
        if (BotGlobalConfig.headless) return

        GL11.glDeleteLists(list, range)
    }

    @JvmStatic
    fun glGetFloat(pname: Int): Float {
        if (BotGlobalConfig.headless) return 0f

        return GL11.glGetFloat(pname)
    }

    @JvmStatic
    fun glFinish() {
        if (BotGlobalConfig.headless) return

        GL11.glFinish()
    }

    @JvmStatic
    fun glCallLists(lists: IntBuffer) {
        if (BotGlobalConfig.headless) return

        GL11.glCallLists(lists)
    }

    @JvmStatic
    fun glLight(light: Int, pname: Int, params: FloatBuffer) {
        if (BotGlobalConfig.headless) return

        GL11.glLight(light, pname, params)
    }

    @JvmStatic
    fun glLightModel(pname: Int, params: FloatBuffer) {
        if (BotGlobalConfig.headless) return

        GL11.glLightModel(pname, params)
    }

    @JvmStatic
    fun glTexCoordPointer(size: Int, stride: Int, pointer: FloatBuffer) {
        if (BotGlobalConfig.headless) return

        GL11.glTexCoordPointer(size, stride, pointer)
    }

    @JvmStatic
    fun glEnableClientState(cap: Int) {
        if (BotGlobalConfig.headless) return

        GL11.glEnableClientState(cap)
    }

    @JvmStatic
    fun glTexCoordPointer(size: Int, stride: Int, pointer: ShortBuffer) {
        if (BotGlobalConfig.headless) return

        GL11.glTexCoordPointer(size, stride, pointer)
    }

    @JvmStatic
    fun glColorPointer(size: Int, unsigned: Boolean, stride: Int, pointer: ByteBuffer) {
        if (BotGlobalConfig.headless) return

        GL11.glColorPointer(size, unsigned, stride, pointer)
    }

    @JvmStatic
    fun glNormalPointer(stride: Int, pointer: ByteBuffer) {
        if (BotGlobalConfig.headless) return

        GL11.glNormalPointer(stride, pointer)
    }

    @JvmStatic
    fun glVertexPointer(size: Int, stride: Int, pointer: FloatBuffer) {
        if (BotGlobalConfig.headless) return

        GL11.glVertexPointer(size, stride, pointer)
    }

    @JvmStatic
    fun glDrawArrays(mode: Int, first: Int, count: Int) {
        if (BotGlobalConfig.headless) return

        GL11.glDrawArrays(mode, first, count)
    }

    @JvmStatic
    fun glDisableClientState(cap: Int) {
        if (BotGlobalConfig.headless) return

        GL11.glDisableClientState(cap)
    }

    @JvmStatic
    fun glDeleteTextures(texture: Int) {
        if (BotGlobalConfig.headless) return

        GL11.glDeleteTextures(texture)
    }

    @JvmStatic
    fun glGenTextures(): Int {
        if (BotGlobalConfig.headless) return 0

        return GL11.glGenTextures()
    }

    @JvmStatic
    fun glTexSubImage2D(
        target: Int, level: Int, xoffset: Int, yoffset: Int, width: Int, height: Int,
        format: Int, type: Int, pixels: IntBuffer
    ) {
        if (BotGlobalConfig.headless) return

        GL11.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, pixels)
    }

    @JvmStatic
    fun glTexParameterf(target: Int, pname: Int, param: Float) {
        if (BotGlobalConfig.headless) return

        GL11.glTexParameterf(target, pname, param)
    }

    @JvmStatic
    fun glTexImage2D(
        target: Int, level: Int, internalformat: Int, width: Int, height: Int, border: Int,
        format: Int, type: Int, pixels: IntBuffer?
    ) {
        if (BotGlobalConfig.headless) return

        GL11.glTexImage2D(target, level, internalformat, width, height, border, format, type, pixels)
    }

    @JvmStatic
    fun glGetTexParameteri(target: Int, pname: Int): Int {
        if (BotGlobalConfig.headless) return 0

        return GL11.glGetTexParameteri(target, pname)
    }

    @JvmStatic
    fun glGetTexParameterf(target: Int, pname: Int): Float {
        if (BotGlobalConfig.headless) return 0f

        return GL11.glGetTexParameterf(target, pname)
    }

    @JvmStatic
    fun glBindTexture(target: Int, texture: Int) {
        if (BotGlobalConfig.headless) return

        GL11.glBindTexture(target, texture)
    }

    @JvmStatic
    fun glTexGeni(coord: Int, pname: Int, param: Int) {
        if (BotGlobalConfig.headless) return

        GL11.glTexGeni(coord, pname, param)
    }

    @JvmStatic
    fun glTexGen(coord: Int, pname: Int, params: FloatBuffer) {
        if (BotGlobalConfig.headless) return

        GL11.glTexGen(coord, pname, params)
    }

    @JvmStatic
    fun glPixelStorei(pname: Int, param: Int) {
        if (BotGlobalConfig.headless) return

        GL11.glPixelStorei(pname, param)
    }

    @JvmStatic
    fun glGetTexImage(target: Int, level: Int, format: Int, type: Int, pixels: IntBuffer) {
        if (BotGlobalConfig.headless) return

        GL11.glGetTexImage(target, level, format, type, pixels)
    }

    @JvmStatic
    fun glReadPixels(x: Int, y: Int, width: Int, height: Int, format: Int, type: Int, pixels: IntBuffer) {
        if (BotGlobalConfig.headless) return

        GL11.glReadPixels(x, y, width, height, format, type, pixels)
    }

    @JvmStatic
    fun glTexSubImage2D(
        target: Int, level: Int, xoffset: Int, yoffset: Int, width: Int, height: Int,
        format: Int, type: Int, pixels: ByteBuffer
    ) {
        if (BotGlobalConfig.headless) return

        GL11.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, pixels)
    }
}
