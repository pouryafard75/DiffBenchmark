package benchmark.utils.Configuration;

import benchmark.generators.tools.ASTDiffTool;

/* Created by pourya on 2023-09-15 5:04 p.m. */
public enum Compatibility {

    Experiment{
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
            return "0.0";
        }
    },

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
//                    ASTDiffTool.RM2,
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
                    ASTDiffTool.GTS,
//                    ASTDiffTool.IAM,
                    ASTDiffTool.VNG,
                    ASTDiffTool.VNS,
                    ASTDiffTool.SMG,
                    ASTDiffTool.SMS,
                    ASTDiffTool.NMG,
                    ASTDiffTool.NMS,
                    ASTDiffTool.CPG,
                    ASTDiffTool.CPS,
                    ASTDiffTool.FTG,
                    ASTDiffTool.FTS,
                    ASTDiffTool.ALG,
                    ASTDiffTool.ALS
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
