package miniRV_single_cycle.Core

import chisel3._
import chisel3.util._
import utils.IROM
import config.Configs._
import _root_.circt.stage.ChiselStage

class InstMemIO extends Bundle {
  val addr = Input(UInt(ADDR_WIDTH.W)) // inst addr, output it to the actual mem
  val inst = Output(UInt(DATA_WIDTH.W))
}

class InstMem extends Module {
  val io = IO(new InstMemIO())
  val irom = Module(new IROM)
  irom.io.a := io.addr((ADDR_WIDTH-1),2)
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
