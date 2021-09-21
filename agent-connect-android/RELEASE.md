# Release Procedure

1. From release branch (or branch with latest update from release branch), update *version code* and *version name* in `app/build.gradle`.
	- Increment *version code* by 1 from previous release.

	- Production *version name* format e.g. `1.16.2`, `1`: revamp, `16`: feature release, `2`: minor patches/fixes.

	- Internal test *version name* format e.g. `1.16.2.0-staging`, i.e. append `.0-staging` after the regular *version name*.

2. Generate and upload staging or production `.aab` to Google Play [internal testing channel](https://play.google.com/apps/publish/?account=6319259342473059561#ManageReleaseTrackPlace:p=sg.searchhouse.agentconnect&appid=4973618098911638898&releaseTrackId=4699584016884002427).

3. Provide internal test [opt-in/download link](https://play.google.com/apps/internaltest/4699584016884002427) if required by PMs/testers.

4. Once PM approved, promote `.aab` from Google Play `internal test` (make sure *version name* without `-staging`) to `production` with official release note.

5. Upload production `.apk` (not `.aab` because it can't be installed) to [BitBucket archive](https://bitbucket.org/streetsinemobileengineers/agent-connect-android/downloads/).

6. Merge (squash), without pull request, the release branch to `production`. Commit message e.g. `v1.0.1`, `v1.0.1 (fix 1)` (if patch).

	Example when release version `1.4` from branch `phase-1.4`:

	In CMD (Windows) / Terminal (Mac), navigate to project root folder, execute following.

	- `git checkout production`

	- `git merge --squash phase-1.4 --strategy-option theirs`
    	*NOTE*: If this release contain deleted files, check and delete them manually.

	- `git commit -m "v1.4"` and push.

7. Create pull request to merge (no squash) release branch to `master`.

8. Push and delete release branch.

9. Inform the backend engineer in charge (now *Tayza*) about the *version name* of the new release.