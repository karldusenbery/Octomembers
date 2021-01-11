package dev.dusenbery.octomembers.ui.teammembers

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import dev.dusenbery.octomembers.R
import dev.dusenbery.octomembers.ui.extensions.hideKeyboard
import dev.dusenbery.octomembers.model.Member
import dev.dusenbery.octomembers.repository.remote.RemoteRepository
import kotlinx.android.synthetic.main.activity_team_members.*


class TeamMembersActivity : AppCompatActivity(), TeamMembersContract.View {

  lateinit var presenter: TeamMembersContract.Presenter
  lateinit var adapter: TeamMemberAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_team_members)

    setupPresenter()
    setupEditText()
    setupShowMembersButton()
    setupRecyclerView()
  }

  private fun setupPresenter() {
    presenter = TeamMembersPresenter(RemoteRepository(), this)
  }

  private fun setupEditText() {
    teamName.setSelection(teamName.text.length)
    teamName.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
      if (actionId == EditorInfo.IME_ACTION_DONE) {
        showMembers.performClick()
        return@OnEditorActionListener true
      }
      false
    })
  }

  private fun setupShowMembersButton() {
    showMembers.setOnClickListener {
      val teamNameValue = teamName.text.toString()
      if (teamNameValue.isNotEmpty()) {
        teamMembersList.hideKeyboard()
        presenter.retrieveAllMembers(teamNameValue)
      } else {
        showTeamNameEmptyError()
      }
    }
  }

  private fun setupRecyclerView() {
    teamMembersList.layoutManager = LinearLayoutManager(this)
    adapter = TeamMemberAdapter(listOf())
    teamMembersList.adapter = adapter
  }

  private fun showTeamNameEmptyError() {
    Toast.makeText(this, getString(R.string.error_team_name_empty), Toast.LENGTH_SHORT).show()
  }

  override fun showMembers(members: List<Member>) {
    adapter.members = members
    adapter.notifyDataSetChanged()
    teamMembersList.visibility = View.VISIBLE
  }

  override fun showErrorRetrievingMembers() {
    Toast.makeText(this, getString(R.string.error_retrieving_team), Toast.LENGTH_SHORT).show()
  }

  override fun clearMembers() {
    adapter.members = listOf()
    adapter.notifyDataSetChanged()
  }

  override fun showLoading() {
    loadingIndicator.visibility = View.VISIBLE
  }

  override fun hideLoading() {
    loadingIndicator.visibility = View.GONE
  }

  override fun disableInput() {
    showMembers.isEnabled = false
  }

  override fun enableInput() {
    showMembers.isEnabled = true
  }

  override fun showEmptyState() {
    emptyState.visibility = View.VISIBLE
    emptyState.text = String.format(getString(R.string.empty_state_format), teamName.text.toString())

  }

  override fun hideEmptyState() {
    emptyState.visibility = View.INVISIBLE
  }

  override fun hideMembers() {
    teamMembersList.visibility = View.INVISIBLE
  }
}
