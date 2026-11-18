#!/usr/bin/env python3
"""Collect transitive UCRT64 DLL dependencies without executing the EXE."""
import os, pathlib, re, shutil, subprocess, sys
exe = pathlib.Path("build/mayhem-desktop.exe")
if not exe.is_file():
    sys.exit("Missing build/mayhem-desktop.exe")
file_info = subprocess.run(["file", str(exe)], capture_output=True, text=True, check=True).stdout
print(file_info)
if "PE32+" not in file_info or "x86-64" not in file_info:
    sys.exit("Not a Windows x64 PE executable. Check CMake compiler/toolchain.")
dest = pathlib.Path("dist/Mayhem Desktop")
dest.mkdir(parents=True, exist_ok=True)
shutil.copy2(exe, dest / exe.name)
# Python on Windows does not interpret MSYS2 /ucrt64 as a real filesystem root.
# Resolve the MSYS2 prefix to a native Windows path using cygpath.
prefix = os.environ.get("MINGW_PREFIX", "/ucrt64")
converted = subprocess.run(["cygpath", "-w", prefix], capture_output=True, text=True, check=True).stdout.strip()
search = pathlib.Path(converted) / "bin"
print("DLL search directory:", search, "exists:", search.is_dir())
if not search.is_dir():
    sys.exit("MSYS2 UCRT64 bin directory missing: " + str(search))
system = {"KERNEL32.DLL","USER32.DLL","GDI32.DLL","ADVAPI32.DLL","SHELL32.DLL","OLE32.DLL","OLEAUT32.DLL","WS2_32.DLL","MSVCRT.DLL","UCRTBASE.DLL","COMDLG32.DLL","COMCTL32.DLL","SHLWAPI.DLL","IMM32.DLL","VERSION.DLL","WINMM.DLL","SETUPAPI.DLL","CRYPT32.DLL","BCRYPT.DLL","NTDLL.DLL","RPCRT4.DLL","IPHLPAPI.DLL","DWMAPI.DLL","OPENGL32.DLL","DNSAPI.DLL","WINHTTP.DLL","SECUR32.DLL","WLDAP32.DLL"}
pending = [exe]
seen = set()
missing = []
while pending:
    target = pending.pop()
    result = subprocess.run(["objdump","-p",str(target)],capture_output=True,text=True)
    if result.returncode:
        sys.exit(f"objdump failed on {target}: {result.stderr}")
    for dll in re.findall(r"^\s*DLL Name:\s*(\S+)", result.stdout, re.M):
        key = dll.upper()
        if key in seen: continue
        seen.add(key)
        if key in system or key.startswith(("API-MS-WIN-","EXT-MS-")): continue
        source = search / dll
        if not source.exists():
            matches = list(search.glob(dll)) or [p for p in search.glob("*.dll") if p.name.lower()==dll.lower()]
            if not matches:
                missing.append(dll)
                continue
            source = matches[0]
        shutil.copy2(source, dest / source.name)
        pending.append(source)
        print("Bundled:", source.name)
if missing:
    sys.exit("Missing non-system DLLs: " + ", ".join(missing))
print("Packaged:", dest)
