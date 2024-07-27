package miniRV_pipe_line.Core

import chisel3._
import chisel3.util._

import miniRV_pipe_line.utils._

class ControllerIO extends Bundle {
  val CSIn = Flipped(new ControlSinal()) // from decoder
  val AluCtl = Flipped(new AluControl()) // to alu
  val DMCtl = Flipped(new DataMemControl()) // to dram
  val RfWselCtl = Flipped(new RfWselControl())
  val CSOut = new ControlSinal()
}

class Controller extends Module {
  val io = IO(new ControllerIO())
  //  alu
  io.AluCtl.isSext := io.CSIn.isSext // alua
  io.AluCtl.isJAL := io.CSIn.isJAL // alub
  io.AluCtl.op := io.CSIn.op
  io.AluCtl.isSigned := io.CSIn.isSigned
  io.AluCtl.isBranch := io.CSIn.isBranch

  // dram
  io.DMCtl.isLoad := io.CSIn.isLoad
  io.DMCtl.ctrlLSType := io.CSIn.ctrlLSType
  io.DMCtl.isSigned := io.CSIn.isSigned
  io.DMCtl.isStore := io.CSIn.isStore

  // rf_wsel
  io.RfWselCtl.isJump := io.CSIn.isJump
  io.RfWselCtl.isLoad := io.CSIn.isLoad
  io.RfWselCtl.isLui := io.CSIn.isLui
  // others
  io.CSOut <> io.CSIn
}
