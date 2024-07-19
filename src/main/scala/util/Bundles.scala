package utils

import chisel3._

import config.Configs._
import utils.OP_TYPES._
import utils.LS_TYPES._

class BundleControl extends Bundle {
    val ctrlJump = Output(Bool())
    val ctrlBranch = Output(Bool())
    val ctrlRegWrite = Output(Bool())
    val ctrlLoad = Output(Bool())
    val ctrlStore = Output(Bool())
    val ctrlALUSrc = Output(Bool())
    val ctrlJAL = Output(Bool())
    val ctrlALU_OP = Output(UInt(OP_TYPES_WIDTH.W))
    val ctrlSigned = Output(Bool())
    val ctrlLSType = Output(UInt(LS_TYPE_WIDTH.W))
}

// class BundleAluControl extends Bundle {
//     val ctrlALUSrc = Input(Bool())
//     val ctrlJAL = Input(Bool())
//     val ctrlOP = Input(UInt(OP_TYPES_WIDTH.W))
//     val ctrlSigned = Input(Bool())
//     val ctrlBranch = Input(Bool())
// }

class BundleMemDataControl extends Bundle {
    val ctrlLoad = Input(Bool())
    val ctrlStore = Input(Bool())
    val ctrlSigned = Input(Bool())
    val ctrlLSType = Input(UInt(LS_TYPE_WIDTH.W))
}
class Alu_Control extends Bundle {
    val aluASel = Input(Bool()) // jal select pc
    val aluBSel = Input(Bool()) // imm and rR2
    val op  = Input(UInt(OP_TYPES_WIDTH.W))
    val isSigned = Input(Bool())// unsigned or signed
    val isBranch = Input(Bool())//to distinguish slt and branch

}
class RegNum extends Bundle {
    val rs1 = Output(UInt(REG_NUMS_LOG.W))
    val rs2 = Output(UInt(REG_NUMS_LOG.W))
    val rd = Output(UInt(REG_NUMS_LOG.W))
}