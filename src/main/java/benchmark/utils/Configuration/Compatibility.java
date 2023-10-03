package benchmark.utils.Configuration;

import benchmark.oracle.generators.tools.models.ASTDiffTool;

/* Created by pourya on 2023-09-15 5:04 p.m. */
public enum Compatibility {
    TwoPointOne {
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

        @Override
        String getVersion() {
            return "2.1";
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

        @Override
        String getVersion() {
            return "3.0";
        }
    };
    abstract ASTDiffTool[] getTools();
    abstract String getVersion();
}
