package miniRV_pipe_line.Core.pipeline_Regs

import chisel3._
import chisel3.util._
import config.Configs._
import _root_.circt.stage.ChiselStage

class IF1_IF2_IO  extends Bundle {
    val stall = Input(Bool())
    val flush = Input(Bool())
    
    val commit_if1 = Input(Bool())
    val pc_if1 = Input(UInt(ADDR_WIDTH.W))

    val commit_if2 = Output(Bool())
    val pc_if2 = Output(Bool())
}

class IF1_IF2 extends Module {
    val io = IO(new IF1_IF2_IO)
    val pc_if2 = RegInit(START_ADDR.U(ADDR_WIDTH.W))
    val commit_if2 = RegInit(false.B)

    when(io.flush)
    {
        pc_if2 := START_ADDR.U(ADDR_WIDTH.W)
        commit_if2 := false.B
    }.elsewhen(!io.stall)
    {
        pc_if2 := io.pc_if1
        commit_if2 := io.commit_if1
    }

    io.pc_if2 := pc_if2
    io.commit_if2 := commit_if2
}

object myIF1_IF2 extends App {
  println(
    ChiselStage.emitSystemVerilog(
      new IF1_IF2 ,
      firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
    )
  )
}


