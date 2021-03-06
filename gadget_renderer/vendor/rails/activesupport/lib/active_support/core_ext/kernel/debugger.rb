module Kernel
  unless respond_to?(:debugger)
    # Starts a debugging session if ruby-debug has been loaded (call script/server --debugger to do load it).
    def debugger
      message = "\n***** Debugger requested, but was not available: Start server with --debugger to enable *****\n"
      defined?(Rails) ? Rails.logger.info(message) : $stderr.puts(message)
    end
  end

  def breakpoint
    message = "\n***** The 'breakpoint' command has been renamed 'debugger' -- please change *****\n"
    defined?(Rails) ? Rails.logger.info(message) : $stderr.puts(message)
    debugger
  end
end
