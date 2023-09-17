package benchmark.utils;

/* Created by pourya on 2023-09-15 5:04 p.m. */
public enum Compatibility {
    TwoPointOneAllTools {
        @Override
        ASTDiffTool[] getTools() {
            return new ASTDiffTool[]{
                    ASTDiffTool.RMD,
                    ASTDiffTool.GTG,
                    ASTDiffTool.GTS,
                    ASTDiffTool.IJM,
                    ASTDiffTool.MTD,
                    ASTDiffTool.GT2,
            };
        }
    },
    ThreePointZero {
        @Override
        ASTDiffTool[] getTools() {
            return new ASTDiffTool[]{
                    ASTDiffTool.RMD,
                    ASTDiffTool.GTG,
                    ASTDiffTool.GTS
            };
        }
    };
    abstract ASTDiffTool[] getTools();
}
