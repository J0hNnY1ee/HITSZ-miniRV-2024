package miniRV_pipe_line.utils
import chisel3._
import chisel3.util._
import config.Configs._
import _root_.circt.stage.ChiselStage


class IROM extends BlackBox {
  val io = IO(new Bundle {
    val a = Input(UInt(ADDR_WIDTH.W))
    val spo = Output(UInt(DATA_WIDTH.W))
  })
}

class DRAM extends BlackBox {
  val io = IO(
    new Bundle {
      val clk = Input(Clock())
      val a = Input(UInt(ADDR_WIDTH.W))
      val spo = Output(UInt(DATA_WIDTH.W)) // get data from dram
      val we = Input(Bool())
      val d = Input(UInt(DATA_WIDTH.W)) // write to dram
    }
  )
}
class cpuclk extends BlackBox{
  val io = IO(new Bundle {
    val clk_in1 = Input(Clock())
    val locked = Output(Bool())
    val clk_out1 = Output(Clock())
  })
}

class TestBlackBoxModule extends Module {
  val io = IO(new Bundle {})
  val irom = Module(new IROM)
  val dram = Module(new DRAM)
  irom.io.a := 0.U
  dram.io.clk := clock
  dram.io.a:= 0.U
  dram.io.we := 0.B
  dram.io.d := 0.U
  // connecting one of IBUFDS's input clock ports to Top's clock signal
}
object TestBlackBox extends App {

  println(
    ChiselStage.emitSystemVerilog(
      new TestBlackBoxModule,
      firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
    )
  )
}
