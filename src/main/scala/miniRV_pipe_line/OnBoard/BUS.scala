package miniRV_pipe_line.OnBoard
import chisel3._
import chisel3.util._
import config.Configs._
import _root_.circt.stage.ChiselStage
import miniRV_pipe_line.utils._

class BusIO extends Bundle {
// cpu - BUS
  val cpu_ctl = new DataMemControl()
  val cpu_addr = Input(UInt(ADDR_WIDTH.W))
  val cpu_wD = Input(UInt(DATA_WIDTH.W))
  val cpu_rD = Output(UInt(DATA_WIDTH.W))

// mem - BUS
  val mem_rD = Input(UInt(DATA_WIDTH.W))
  val mem_wD = Output(UInt(DATA_WIDTH.W))
  val mem_addr = Output(UInt(ADDR_WIDTH.W))
  val mem_ctl = Flipped(new DataMemControl())

// peripheral device - BUS
  val pd_rD = Input(UInt(DATA_WIDTH.W))

  val pd_we = Output(Bool())
  val pd_addr = Output(UInt(ADDR_WIDTH.W))
  val pd_wD = Output(UInt(DATA_WIDTH.W))

}

class Bus extends Module {
  val io = IO(new BusIO)

  val pd_en =
    io.cpu_addr === PERI_ADDR_BTN || io.cpu_addr === PERI_ADDR_DIG || io.cpu_addr === PERI_ADDR_LED || io.cpu_addr === PERI_ADDR_SW

  io.mem_ctl.ctrlLSType := io.cpu_ctl.ctrlLSType
  io.mem_ctl.isSigned := io.cpu_ctl.isSigned
  io.mem_ctl.isLoad := io.cpu_ctl.isLoad
  io.mem_addr := io.cpu_addr
  io.mem_wD := io.cpu_wD
  io.pd_wD := io.cpu_wD
  io.pd_addr := io.cpu_addr

  // mem or pd
  when(pd_en) {
    io.mem_ctl.isStore := false.B
    io.cpu_rD := io.pd_rD
    io.pd_we := true.B

  }.otherwise {

    io.pd_we := false.B
    io.cpu_rD := io.mem_rD
    io.mem_ctl.isStore := io.cpu_ctl.isStore
  }

}

object myBus extends App {
  println(
    ChiselStage.emitSystemVerilog(
      new Bus,
      firtoolOpts = Array("-disable-all-randomization", "-strip-debug-info")
    )
  )
}


