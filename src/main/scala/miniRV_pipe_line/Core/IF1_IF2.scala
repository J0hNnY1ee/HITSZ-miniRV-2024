package miniRV_pipe_line.Core

import chisel3._
import chisel3.util._
import miniRV_pipe_line.utils.IROM
import config.Configs._
import _root_.circt.stage.ChiselStage

// IROM
class InstMemIO extends Bundle {
  val flush = Input(Bool())
  val stall = Input(Bool())
  val addr = Input(UInt(ADDR_WIDTH.W)) // inst addr, output it to the actual mem
  val inst = Output(UInt(DATA_WIDTH.W))
}

class InstMem extends Module {
  val io = IO(new InstMemIO())
  val irom = Module(new IROM)
  irom.io.a := io.addr((ADDR_WIDTH - 1), 2)
  io.inst := irom.io.spo
}




















object myInstMem extends App {
  println(
    ChiselStage.emitSystemVerilog(
      new InstMem,
      firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
    )
  )
}
