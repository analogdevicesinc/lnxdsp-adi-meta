#!/bin/bash
# Fail if kernel/U-Boot branches or SRCREVs pinned in docs/ differ from the recipes.
# shellcheck disable=SC2016
set -eu
cd "$(dirname "$0")/../.."

recipes=meta-adi-adsp-sc5xx
linux_bb=$recipes/recipes-kernel/linux/linux-adi_%.bb
uboot_inc=$recipes/recipes-bsp/u-boot/u-boot-adi.inc
uboot_bb=$(ls $recipes/recipes-bsp/u-boot/u-boot-adi_*.bb)

var() {
	local v
	v=$(sed -nE "s/^$1[[:space:]]*\??\??=[[:space:]]*\"([^\"]*)\".*/\1/p" "$2")
	[[ -n $v && $v != *$'\n'* ]] || { echo "::error file=$2::cannot read $1" >&2; exit 1; }
	echo "$v"
}

linux_branch=$(var KERNEL_BRANCH "$linux_bb")
linux_version=$(var LINUX_VERSION "$linux_bb")
linux_branch=${linux_branch/'${LINUX_VERSION}'/$linux_version}
linux_srcrev=$(var SRCREV "$linux_bb")
uboot_branch=$(var UBOOT_BRANCH "$uboot_inc")
uboot_srcrev=$(var SRCREV "$uboot_bb")

fail=0
check() {
	while IFS=: read -r file line match; do
		[[ $match == *"$1"* ]] && continue
		echo "::error file=$file,line=$line::$match: $3"
		fail=1
	done < <(grep -rnoE --include='*.rst' "$2" docs || true)
}

check "$linux_branch" 'adsp-[0-9.]+-y' "kernel branch should be $linux_branch"
check "$linux_srcrev" ':git-linux:`[^`]*<[0-9a-f]{40}' \
	"kernel SRCREV is $linux_srcrev; update it and re-check the #L anchors"
check "$uboot_branch" 'adi-u-boot-[0-9.]+y' "U-Boot branch should be $uboot_branch"
check "$uboot_srcrev" ':git-u-boot:`[^`]*<[0-9a-f]{40}' \
	"U-Boot SRCREV is $uboot_srcrev; update it and re-check the #L anchors"

exit $fail
